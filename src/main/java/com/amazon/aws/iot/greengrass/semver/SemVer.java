/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 *
 *
 * Translated from node-semver:
 *
 * The ISC License
 * Copyright (c) Isaac Z. Schlueter and Contributors
 *
 * See com.amazon.aws.iot.greengrass.semver.THIRD-PARTY-LICENSE
 */

package com.amazon.aws.iot.greengrass.semver;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SemVer implements Comparable<SemVer> {
    private static final Pattern IS_NUMERIC = Pattern.compile("^[0-9]+$");
    private final String raw;
    @Getter
    private String version;
    public final int major;
    public final int minor;
    public final int patch;
    public final List<Object> prerelease;
    private final List<String> build;

    SemVer(int major, int minor, int patch, List<Object> prerelease, List<String> build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prerelease = prerelease;
        this.build = build;
        this.raw = this.format();
        this.version = this.raw;
    }

    SemVer(SemVer other) {
        this(other.major, other.minor, other.patch, new ArrayList<>(other.prerelease), new ArrayList<>(other.build));
    }

    // loose and includePrerelease are false
    public SemVer(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Version \"null\" is not valid");
        }
        Matcher matcher = SemVerRegexes.re.get(SemVerRegexes.t.get("FULL")).matcher(version.trim());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Version \"" + version + "\" is not valid");
        }
        MatchResult m = matcher.toMatchResult();

        this.raw = version;

        // these are actually numbers
        this.major = Integer.parseInt(m.group(1));
        this.minor = Integer.parseInt(m.group(2));
        this.patch = Integer.parseInt(m.group(3));

        if (this.major < 0) {
            throw new IllegalArgumentException("Invalid major version");
        }

        if (this.minor < 0) {
            throw new IllegalArgumentException("Invalid minor version");
        }

        if (this.patch < 0) {
            throw new IllegalArgumentException("Invalid patch version");
        }

        // numberify any prerelease numeric ids
        if (m.group(4) != null) {
            this.prerelease = Arrays.stream(m.group(4).split("\\.", -1)).map((String id) -> {
                if (IS_NUMERIC.matcher(id).find()) {
                    int num = Integer.parseInt(id);
                    if (num >= 0) {
                        return num;
                    }
                }
                return id;
            }).collect(Collectors.toList());
        } else {
            this.prerelease = new ArrayList<>();
        }

        this.build = m.group(5) == null ? new ArrayList<>() : Arrays.asList(m.group(5).split("\\.", -1));
        this.version = this.format();
    }

    protected String format() {
        String v = String.format("%s.%s.%s", this.major, this.minor, this.patch);
        if (this.prerelease != null && this.prerelease.size() > 0) {
            v += String.format("-%s",
                    this.prerelease.stream().map(Objects::toString).collect(Collectors.joining(".")));
        }
        this.version = v;
        return v;
    }

    /**
     * Get the normalized value of the SemVer including prerelease and build
     * @return String.
     */
    public String getValue() {
        if (build.isEmpty()) {
            return version;
        }
        return String.format("%s+%s", version, String.join(".", build));
    }

    @Override
    public int compareTo(SemVer other) {
        if (other.version.equals(this.version)) {
            return 0;
        }

        int mainCompared = this.compareMain(other);
        if (mainCompared != 0) {
            return mainCompared;
        }
        return this.comparePre(other);
    }

    public int compareMain(SemVer other) {
        int maj = compareIdentifiers(this.major, other.major);
        if (maj != 0) {
            return maj;
        }
        int min = compareIdentifiers(this.minor, other.minor);
        if (min != 0) {
            return min;
        }
        return compareIdentifiers(this.patch, other.patch);
    }

    public int comparePre(SemVer other) {
        // NOT having a prerelease is > having one
        if (!this.prerelease.isEmpty() && other.prerelease.isEmpty()) {
            return -1;
        } else if (this.prerelease.isEmpty() && !other.prerelease.isEmpty()) {
            return 1;
        } else if (this.prerelease.isEmpty() && other.prerelease.isEmpty()) {
            return 0;
        }

        for (int i = 0; ; i++) {
            if (i > this.prerelease.size() - 1 && i > other.prerelease.size() - 1) {
                return 0;
            } else if (i > other.prerelease.size() - 1) {
                return 1;
            } else if (i > this.prerelease.size() - 1) {
                return -1;
            }
            Object a = this.prerelease.get(i);
            Object b = other.prerelease.get(i);
            if (Objects.equals(a, b)) {
                continue;
            }
            return compareIdentifiers(Objects.toString(a), Objects.toString(b));
        }
    }

    public int compareBuild(SemVer other) {
        for (int i = 0; ; i++) {
            if (i > this.build.size() - 1 && i > other.build.size() - 1) {
                return 0;
            } else if (i > other.build.size() - 1) {
                return 1;
            } else if (i > this.build.size() - 1) {
                return -1;
            }
            String a = this.build.get(i);
            String b = other.build.get(i);
            if (Objects.equals(a, b)) {
                continue;
            } else {
                return compareIdentifiers(a, b);
            }
        }
    }

    static Integer isInt(String x) {
        try {
            return Integer.parseInt(x);
        } catch (NumberFormatException f) {
            return null;
        }
    }

    public static int compareIdentifiers(int a, int b) {
        return Integer.compare(a, b);
    }

    public static int compareIdentifiers(String a, String b) {
        Integer anum = isInt(a);
        Integer bnum = isInt(b);

        int comparison = a.equals(b) ? 0 : (anum != null && bnum == null) ? -1 : (bnum != null && anum == null) ? 1
                : anum != null && bnum != null ? (anum < bnum ? -1 : 1) : a.compareTo(b);
        if (comparison >= 1) {
            return 1;
        } else if (comparison == 0) {
            return 0;
        }
        return -1;
    }

    // preminor will bump the version up to the next minor release, and immediately
    // down to pre-release. premajor and prepatch work the same way.
    public SemVer inc(String release, String identifier) {
        switch (release) {
            case "premajor": {
                SemVer v = new SemVer(major + 1, 0, 0, new ArrayList<>(), new ArrayList<>(this.build));
                return v.inc("pre", identifier);
            }
            case "preminor": {
                SemVer v = new SemVer(major, minor + 1, 0, new ArrayList<>(), new ArrayList<>(this.build));
                return v.inc("pre", identifier);
            }
            case "prepatch": {
                // If this is already a prerelease, it will bump to the next version
                // drop any prereleases that might already exist, since they are not
                // relevant at this point.
                SemVer v = new SemVer(major, minor, patch, new ArrayList<>(), new ArrayList<>(this.build));
                return v.inc("patch", identifier).inc("pre", identifier);
            }
            // If the input is a non-prerelease version, this acts the same as
            // prepatch.
            case "prerelease": {
                SemVer v = new SemVer(this);
                if (v.prerelease.isEmpty()) {
                    v = v.inc("patch", identifier);
                }
                return v.inc("pre", identifier);
            }
            case "major": {
                // If this is a pre-major version, bump up to the same major version.
                // Otherwise increment major.
                // 1.0.0-5 bumps to 1.0.0
                // 1.1.0 bumps to 2.0.0
                SemVer v = new SemVer(this);
                if (this.minor != 0 || this.patch != 0 || this.prerelease.size() == 0) {
                    v = new SemVer(v.major + 1, v.minor, v.patch, v.prerelease, v.build);
                }
                v = new SemVer(v.major, 0, 0, new ArrayList<>(), v.build);
                return v;
            }
            case "minor": {
                // If this is a pre-minor version, bump up to the same minor version.
                // Otherwise increment minor.
                // 1.2.0-5 bumps to 1.2.0
                // 1.2.1 bumps to 1.3.0
                SemVer v = new SemVer(this);
                if (this.patch != 0 || this.prerelease.size() == 0) {
                    v = new SemVer(v.major, v.minor + 1, v.patch, v.prerelease, v.build);
                }
                return new SemVer(v.major, v.minor, 0, new ArrayList<>(), v.build);
            }
            case "patch": {
                // If this is not a pre-release version, it will increment the patch.
                // If it is a pre-release it will bump up to the same patch version.
                // 1.2.0-5 patches to 1.2.0
                // 1.2.0 patches to 1.2.1
                SemVer v = new SemVer(this);
                if (v.prerelease.isEmpty()) {
                    v = new SemVer(v.major, v.minor, v.patch + 1, v.prerelease, v.build);
                }
                return new SemVer(v.major, v.minor, v.patch, new ArrayList<>(), v.build);
            }
            // This probably shouldn't be used publicly.
            // 1.0.0 'pre' would become 1.0.0-0 which is the wrong direction.
            case "pre": {
                SemVer v = new SemVer(this);
                if (v.prerelease.isEmpty()) {
                    v.prerelease.add(0);
                } else {
                    int i = v.prerelease.size();
                    while (--i >= 0) {
                        if (v.prerelease.get(i) instanceof Integer) {
                            v.prerelease.set(i, ((int) v.prerelease.get(i)) + 1);
                            i = -2;
                        }
                    }
                    if (i == -1) {
                        // didn't increment anything
                        v.prerelease.add(0);
                    }
                }
                if (identifier != null) {
                    // 1.2.0-beta.1 bumps to 1.2.0-beta.2,
                    // 1.2.0-beta.fooblz or 1.2.0-beta bumps to 1.2.0-beta.0
                    if (!v.prerelease.isEmpty() && Objects.equals(v.prerelease.get(0), identifier)) {
                        if (v.prerelease.size() > 1 && !(v.prerelease.get(1) instanceof Number)) {
                            v.prerelease.clear();
                            v.prerelease.add(identifier);
                            v.prerelease.add(0);
                        }
                    } else {
                        v.prerelease.clear();
                        v.prerelease.add(identifier);
                        v.prerelease.add(0);
                    }
                }
                v.format();
                return v;
            }
            default:
                throw new IllegalArgumentException("invalid increment argument: ${release}");
        }
    }

    @Override
    public String toString() {
        return this.version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SemVer) {
            return compareTo((SemVer) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, prerelease, build);
    }
}
