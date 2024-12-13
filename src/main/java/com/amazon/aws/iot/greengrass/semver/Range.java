/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 *
 * Translated from node-semver:
 *
 * The ISC License
 * Copyright (c) Isaac Z. Schlueter and Contributors
 *
 * See com.amazon.aws.iot.greengrass.semver.THIRD-PARTY-LICENSE
 */

package com.amazon.aws.iot.greengrass.semver;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.amazon.aws.iot.greengrass.semver.Range.FreeFunctions.isAny;
import static com.amazon.aws.iot.greengrass.semver.Range.FreeFunctions.isNullSet;
import static com.amazon.aws.iot.greengrass.semver.Range.FreeFunctions.isSatisfiable;
import static com.amazon.aws.iot.greengrass.semver.Range.FreeFunctions.testSet;
import static com.amazon.aws.iot.greengrass.semver.SemVerRegexes.caretTrimReplace;
import static com.amazon.aws.iot.greengrass.semver.SemVerRegexes.comparatorTrimReplace;
import static com.amazon.aws.iot.greengrass.semver.SemVerRegexes.tildeTrimReplace;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Range {
    private final String raw;
    @EqualsAndHashCode.Include
    public String range;
    private List<List<Comparator>> set;
    private static final Pattern S_PLUS = Pattern.compile("\\s+");

    public Range(String range) {
        // First, split based on boolean or ||
        this.raw = range;
        this.set = Arrays.stream(range.split("\\s*\\|\\|\\s*", -1))
                // map the range to a 2d array of comparators
                .map(r -> this.parseRange(r.trim()))
                // throw out any comparator lists that are empty
                // this generally means that it was not a valid range, which is allowed
                // in loose mode, but will still throw if the WHOLE range is invalid.
                .filter(c -> !c.isEmpty()).collect(Collectors.toList());

        if (this.set.isEmpty()) {
            throw new IllegalArgumentException("Invalid SemVer range " + range);
        }

        // if we have any that are not the null set, throw out null sets.
        if (this.set.size() > 1) {
            // keep the first one, in case they're all null sets
            List<Comparator> first = this.set.get(0);
            this.set = this.set.stream().filter(c -> !isNullSet(c.get(0))).collect(Collectors.toList());
            if (this.set.size() == 0) {
                this.set = new ArrayList<List<Comparator>>() {{
                    add(first);
                }};
            } else if (this.set.size() > 1) {
                // if we have any that are *, then the range is just *
                for (List<Comparator> c : this.set) {
                    if (c.size() == 1 && isAny(c.get(0))) {
                        this.set = new ArrayList<List<Comparator>>() {{
                            add(c);
                        }};
                        break;
                    }
                }
            }
        }

        this.format();
    }

    protected String format() {
        this.range = this.set.stream().map((comps) -> comps.stream().map(c -> c.value).collect(Collectors.joining(" ")))
                .collect(Collectors.joining("||")).trim();
        return this.range;
    }

    private List<Comparator> parseRange(String range) {
        // `1.2.3 - 1.2.4` => `>=1.2.3 <=1.2.4`
        Pattern hr = SemVerRegexes.re.get(SemVerRegexes.t.get("HYPHENRANGE"));
        Matcher hyphenMatcher = hr.matcher(range);
        if (hyphenMatcher.find()) {
            int s = hyphenMatcher.start(0);
            int e = hyphenMatcher.end(0);
            range = range.substring(0, s) + FreeFunctions.hyphenReplace(hyphenMatcher.group(0), hyphenMatcher.group(1),
                    hyphenMatcher.group(2), hyphenMatcher.group(3), hyphenMatcher.group(4), hyphenMatcher.group(5),
                    hyphenMatcher.group(6), hyphenMatcher.group(7), hyphenMatcher.group(8), hyphenMatcher.group(9),
                    hyphenMatcher.group(10), hyphenMatcher.group(11), hyphenMatcher.group(12)) + range.substring(e);
        }


        // `> 1.2.3 < 1.2.5` => `>1.2.3 <1.2.5`
        Matcher comparatorMatcher = SemVerRegexes.re.get(SemVerRegexes.t.get("COMPARATORTRIM")).matcher(range);
        if (comparatorMatcher.find()) {
            range = comparatorMatcher.replaceAll(comparatorTrimReplace);
        }

        // `~ 1.2.3` => `~1.2.3`
        Matcher tildeMatcher = SemVerRegexes.re.get(SemVerRegexes.t.get("TILDETRIM")).matcher(range);
        if (tildeMatcher.find()) {
            range = tildeMatcher.replaceAll(tildeTrimReplace);
        }

        // `^ 1.2.3` => `^1.2.3`
        Matcher caretMatcher = SemVerRegexes.re.get(SemVerRegexes.t.get("CARETTRIM")).matcher(range);
        if (caretMatcher.find()) {
            range = caretMatcher.replaceAll(caretTrimReplace);
        }

        // At this point, the range is completely trimmed and
        // ready to be split into comparators.
        List<Comparator> rangeList =
                Arrays.stream(S_PLUS.split(
                    Arrays.stream(S_PLUS.split(range, -1))
                        .map(FreeFunctions::parseComparator)
                    .collect(Collectors.joining(" ")),
            -1))
                // >=0.0.0 is equivalent to *
                .map(FreeFunctions::replaceGTE0)
                .map(Comparator::new)
                .collect(Collectors.toList());

        // if any comparators are the null set, then replace with JUST null set
        // if more than one comparator, remove any * comparators
        // also, don't include the same comparator more than once
        Map<String, Comparator> rangeMap = new LinkedHashMap<>();
        for (Comparator comp : rangeList) {
            if (isNullSet(comp)) {
                return new ArrayList<Comparator>() {{
                    add(comp);
                }};
            }
            rangeMap.put(comp.value, comp);
        }
        if (rangeMap.size() > 1) {
            rangeMap.remove("");
        }

        return new ArrayList<>(rangeMap.values());
    }

    public boolean intersects(Range range) {
        return this.set.stream().anyMatch((thisComparators) -> (isSatisfiable(thisComparators) && range.set.stream()
                .anyMatch((rangeComparators) -> (isSatisfiable(rangeComparators) && thisComparators.stream().allMatch(
                        (thisComparator) -> rangeComparators.stream().allMatch(thisComparator::intersects))))));
    }

    // if ANY of the sets match ALL of its comparators, then pass
    public boolean test(SemVer version) {
        if (version == null) {
            return false;
        }

        for (List<Comparator> comparators : this.set) {
            if (testSet(comparators, version)) {
                return true;
            }
        }
        return false;
    }

    static class FreeFunctions {

        static boolean isNullSet(Comparator c) {
            return c.value.equals("<0.0.0-0");
        }

        static boolean isAny(Comparator c) {
            return c.value.equals("");
        }

        // take a set of comparators and determine whether there
        // exists a version which can satisfy it
        static boolean isSatisfiable(List<Comparator> comparators) {
            boolean result = true;
            if (comparators.isEmpty()) {
                return true;
            }
            List<Comparator> remainingComparators = new ArrayList<>(comparators);
            Comparator testComparator = remainingComparators.remove(remainingComparators.size() - 1);

            while (result && !remainingComparators.isEmpty()) {
                Comparator finalTestComparator = testComparator;
                result = remainingComparators.stream().allMatch(finalTestComparator::intersects);
                testComparator = remainingComparators.remove(remainingComparators.size() - 1);
            }

            return result;
        }

        // comprised of xranges, tildes, stars, and gtlt's at this point.
        // already replaced the hyphen ranges
        // turn into a set of JUST comparators.
        static String parseComparator(String comp) {
            comp = replaceCarets(comp);
            comp = replaceTildes(comp);
            comp = replaceXRanges(comp);
            comp = replaceStars(comp);
            return comp;
        }

        static boolean isX(String id) {
            return id == null || id.trim().isEmpty() || id.equalsIgnoreCase("x") || id.equals("*");
        }

        static String replaceXRanges(String comp) {
            return Arrays.stream(S_PLUS.split(comp.trim(), -1))
                    .map(FreeFunctions::replaceXRange)
                    .collect(Collectors.joining(" "));
        }

        private static String replaceXRange(String s) {
            Pattern r = SemVerRegexes.re.get(SemVerRegexes.t.get("XRANGE"));
            Matcher mm = r.matcher(s);
            if (!mm.find()) {
                return s;
            }

            String gtlt = mm.group(1);
            String M = mm.group(2);
            String m = mm.group(3);
            String p = mm.group(4);
            String pr = mm.group(5);

            String ret = s;
            boolean xM = isX(M);
            boolean xm = xM || isX(m);
            boolean xp = xm || isX(p);
            boolean anyX = xp;

            if ("=".equals(gtlt) && anyX) {
                gtlt = "";
            }

            pr = "";

            if (xM) {
                if (">".equals(gtlt) || "<".equals(gtlt)) {
                    // nothing is allowed
                    ret = "<0.0.0-0";
                } else {
                    // nothing is forbidden
                    ret = "*";
                }
            } else if (!gtlt.isEmpty() && anyX) {
                // we know patch is an x, because we have any x at all.
                // replace X with 0
                if (xm) {
                    m = "0";
                }
                p = "0";

                if (">".equals(gtlt)) {
                    // >1 => >=2.0.0
                    // >1.2 => >=1.3.0
                    gtlt = ">=";
                    if (xm) {
                        M = String.valueOf(Integer.parseInt(M) + 1);
                        m = "0";
                        p = "0";
                    } else {
                        m = String.valueOf(Integer.parseInt(m) + 1);
                        p = "0";
                    }
                } else if ("<=".equals(gtlt)) {
                    // <=0.7.x is actually <0.8.0, since any 0.7.x should
                    // pass.  Similarly, <=7.x is actually <8.0.0, etc.
                    gtlt = "<";
                    if (xm) {
                        M = String.valueOf(Integer.parseInt(M) + 1);
                    } else {
                        m = String.valueOf(Integer.parseInt(m) + 1);
                    }
                }

                if ("<".equals(gtlt)) {
                    pr = "-0";
                }

                ret = String.format("%s%s.%s.%s%s", gtlt, M, m, p, pr);
            } else if (xm) {
                ret = String.format(">=%s.0.0%s <%s.0.0-0", M, pr, Integer.parseInt(M) + 1);
            } else if (xp) {
                ret = String.format(">=%s.%s.0%s <%s.%s.0-0", M, m, pr, M, Integer.parseInt(m) + 1);
            }

            return ret;
        }

        // ~, ~> --> * (any, kinda silly)
        // ~2, ~2.x, ~2.x.x, ~>2, ~>2.x ~>2.x.x --> >=2.0.0 <3.0.0-0
        // ~2.0, ~2.0.x, ~>2.0, ~>2.0.x --> >=2.0.0 <2.1.0-0
        // ~1.2, ~1.2.x, ~>1.2, ~>1.2.x --> >=1.2.0 <1.3.0-0
        // ~1.2.3, ~>1.2.3 --> >=1.2.3 <1.3.0-0
        // ~1.2.0, ~>1.2.0 --> >=1.2.0 <1.3.0-0
        static String replaceTildes(String comp) {
            return Arrays.stream(S_PLUS.split(comp.trim(), -1)).map(FreeFunctions::replaceTilde)
                    .collect(Collectors.joining(" "));
        }

        private static String replaceTilde(String s) {
            Pattern r = SemVerRegexes.re.get(SemVerRegexes.t.get("TILDE"));
            Matcher mm = r.matcher(s);
            if (!mm.find()) {
                return s;
            }

            String M = mm.group(1);
            String m = mm.group(2);
            String p = mm.group(3);
            String pr = mm.group(4);

            String ret;
            if (isX(M)) {
                ret = "";
            } else if (isX(m)) {
                ret = String.format(">=%s.0.0 <%s.0.0-0", M, Integer.parseInt(M) + 1);
            } else if (isX(p)) {
                // ~1.2 == >=1.2.0 <1.3.0-0
                ret = String.format(">=%s.%s.0 <%s.%s.0-0", M, m, M, Integer.parseInt(m) + 1);
            } else if (pr != null) {
                ret = String.format(">=%s.%s.%s-%s <%s.%s.0-0", M, m, p, pr, M,
                        Integer.parseInt(m) + 1);
            } else {
                // ~1.2.3 == >=1.2.3 <1.3.0-0
                ret = String.format(">=%s.%s.%s <%s.%s.0-0", M, m, p, M, Integer.parseInt(m) + 1);
            }

            return ret;
        }

        // ^ --> * (any, kinda silly)
        // ^2, ^2.x, ^2.x.x --> >=2.0.0 <3.0.0-0
        // ^2.0, ^2.0.x --> >=2.0.0 <3.0.0-0
        // ^1.2, ^1.2.x --> >=1.2.0 <2.0.0-0
        // ^1.2.3 --> >=1.2.3 <2.0.0-0
        // ^1.2.0 --> >=1.2.0 <2.0.0-0
        static String replaceCarets(String comp) {
            return Arrays.stream(S_PLUS.split(comp.trim(), -1)).map(FreeFunctions::replaceCaret)
                    .collect(Collectors.joining(" "));
        }

        static String replaceCaret(String c) {
            Pattern r = SemVerRegexes.re.get(SemVerRegexes.t.get("CARET"));
            Matcher mm = r.matcher(c);
            if (!mm.find()) {
                return c;
            }

            String M = mm.group(1);
            String m = mm.group(2);
            String p = mm.group(3);
            String pr = mm.group(4);

            String ret;
            if (isX(M)) {
                ret = "";
            } else if (isX(m)) {
                ret = String.format(">=%s.0.0 <%s.0.0-0", M, Integer.parseInt(M) + 1);
            } else if (isX(p)) {
                if ("0".equals(M)) {
                    ret = String.format(">=%s.%s.0 <%s.%s.0-0", M, m, M, Integer.parseInt(m) + 1);
                } else {
                    ret = String.format(">=%s.%s.0 <%s.0.0-0", M, m, Integer.parseInt(M) + 1);
                }
            } else if (pr != null) {
                if ("0".equals(M)) {
                    if ("0".equals(m)) {
                        ret = String.format(">=%s.%s.%s-%s <%s.%s.%s-0", M, m, p, pr, M, m, Integer.parseInt(p) + 1);
                    } else {
                        ret = String.format(">=%s.%s.%s-%s <%s.%s.0-0", M, m, p, pr, M, Integer.parseInt(m) + 1);
                    }
                } else {
                    ret = String.format(">=%s.%s.%s-%s <%s.0.0-0", M, m, p, pr, Integer.parseInt(M) + 1);
                }
            } else {
                if ("0".equals(M)) {
                    if ("0".equals(m)) {
                        ret = String.format(">=%s.%s.%s <%s.%s.%s-0", M, m, p, M, m, Integer.parseInt(p) + 1);
                    } else {
                        ret = String.format(">=%s.%s.%s <%s.%s.0-0", M, m, p, M, Integer.parseInt(m) + 1);
                    }
                } else {
                    ret = String.format(">=%s.%s.%s <%s.0.0-0", M, m, p, Integer.parseInt(M) + 1);
                }
            }

            return ret;
        }


        // Because * is AND-ed with everything else in the comparator,
        // and '' means "any version", just remove the *s entirely.
        static String replaceStars(String comp) {
            // Looseness is ignored here.  star is always as loose as it gets!
            return SemVerRegexes.re.get(SemVerRegexes.t.get("STAR")).matcher(comp).replaceAll("");
        }

        static String replaceGTE0(String comp) {
            return SemVerRegexes.re.get(SemVerRegexes.t.get("GTE0")).matcher(comp).replaceAll("");
        }

        // This function is passed to string.replace(re[t.HYPHENRANGE])
        // M, m, patch, prerelease, build
        // 1.2 - 3.4.5 => >=1.2.0 <=3.4.5
        // 1.2.3 - 3.4 => >=1.2.0 <3.5.0-0 Any 3.4.x will do
        // 1.2 - 3.4 => >=1.2.0 <3.5.0-0
        private static String hyphenReplace(String $0, String from, String fM, String fm, String fp, String fpr,
                                            String fb, String to, String tM, String tm, String tp, String tpr,
                                            String tb) {
            if (isX(fM)) {
                from = "";
            } else if (isX(fm)) {
                from = String.format(">=%s.0.0", fM);
            } else if (isX(fp)) {
                from = String.format(">=%s.%s.0", fM, fm);
            } else {
                from = String.format(">=%s", from);
            }

            if (isX(tM)) {
                to = "";
            } else if (isX(tm)) {
                to = String.format("<%s.0.0-0", Integer.parseInt(tM) + 1);
            } else if (isX(tp)) {
                to = String.format("<%s.%s.0-0", tM, Integer.parseInt(tm) + 1);
            } else if (tpr != null) {
                to = String.format("<=%s.%s.%s-%s", tM, tm, tp, tpr);
            } else {
                to = String.format("<=%s", to);
            }

            return String.format("%s %s", from, to).trim();
        }

        static boolean testSet(List<Comparator> set, SemVer version) {
            for (Comparator comparator : set) {
                if (!comparator.test(version)) {
                    return false;
                }
            }

            if (!version.prerelease.isEmpty()) {
                // Find the set of versions that are allowed to have prereleases
                // For example, ^1.2.3-pr.1 desugars to >=1.2.3-pr.1 <2.0.0
                // That should allow `1.2.3-pr.2` to pass.
                // However, `1.2.4-alpha.notready` should NOT be allowed,
                // even though it's within the range set by the comparators.
                for (Comparator comparator : set) {
                    if (comparator.semver == Comparator.ANY) {
                        continue;
                    }

                    if (comparator.semver.prerelease.size() > 0) {
                        SemVer allowed = comparator.semver;
                        if (allowed.major == version.major && allowed.minor == version.minor
                                && allowed.patch == version.patch) {
                            return true;
                        }
                    }
                }

                // Version has a -pre, but it's not one of the ones we like.
                return false;
            }

            return true;
        }
    }

    @Override
    public String toString() {
        return this.range;
    }
}

