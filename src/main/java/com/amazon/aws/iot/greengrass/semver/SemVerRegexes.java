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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SemVerRegexes {
    static int R = 0;
    public static final Map<String, Integer> t = new HashMap<>();
    public static final List<String> src = new ArrayList<>();
    public static final  List<Pattern> re = new ArrayList<>();

    public static final String tildeTrimReplace = "$1~";
    public static final String caretTrimReplace = "$1^";
    public static final String comparatorTrimReplace = "$1$2$3";

    static {
        // The following Regular Expressions can be used for tokenizing,
        // validating, and parsing SemVer version strings.

        // ## Numeric Identifier
        // A single `0`, or a non-zero digit followed by zero or more digits.

        createToken("NUMERICIDENTIFIER", "0|[1-9]\\d*");
        createToken("NUMERICIDENTIFIERLOOSE", "[0-9]+");

        // ## Non-numeric Identifier
        // Zero or more digits, followed by a letter or hyphen, and then zero or
        // more letters, digits, or hyphens.

        createToken("NONNUMERICIDENTIFIER", "\\d*[a-zA-Z-][a-zA-Z0-9-]*");

        // ## Main Version
        // Three dot-separated numeric identifiers.

        createToken("MAINVERSION", String.format("(%s)\\.(%s)\\.(%s)", src.get(t.get("NUMERICIDENTIFIER")),
                src.get(t.get("NUMERICIDENTIFIER")), src.get(t.get("NUMERICIDENTIFIER"))));

        createToken("MAINVERSIONLOOSE", String.format("(%s)\\.(%s)\\.(%s)",
                        src.get(t.get("NUMERICIDENTIFIERLOOSE")), src.get(t.get("NUMERICIDENTIFIERLOOSE")),
                        src.get(t.get("NUMERICIDENTIFIERLOOSE"))));

        // ## Pre-release Version Identifier
        // A numeric identifier, or a non-numeric identifier.

        createToken("PRERELEASEIDENTIFIER", String.format("(?:%s|%s)",
                src.get(t.get("NUMERICIDENTIFIER")),
                src.get(t.get("NONNUMERICIDENTIFIER"))));

        createToken("PRERELEASEIDENTIFIERLOOSE", String.format("(?:%s|%s)",
                src.get(t.get("NUMERICIDENTIFIERLOOSE")),
                src.get(t.get("NONNUMERICIDENTIFIER"))));

        // ## Pre-release Version
        // Hyphen, followed by one or more dot-separated pre-release version
        // identifiers.

        createToken("PRERELEASE", String.format("(?:-(%s(?:\\.%s)*))",
                src.get(t.get("PRERELEASEIDENTIFIER")),
                src.get(t.get("PRERELEASEIDENTIFIER"))));

        createToken("PRERELEASELOOSE", String.format("(?:-?(%s(?:\\.%s)*))",
                src.get(t.get("PRERELEASEIDENTIFIERLOOSE")), src.get(t.get("PRERELEASEIDENTIFIERLOOSE"))));

        // ## Build Metadata Identifier
        // Any combination of digits, letters, or hyphens.

        createToken("BUILDIDENTIFIER", "[0-9A-Za-z-]+");

        // ## Build Metadata
        // Plus sign, followed by one or more period-separated build metadata
        // identifiers.

        createToken("BUILD", String.format("(?:\\+(%s(?:\\.%s)*))",
                src.get(t.get("BUILDIDENTIFIER")), src.get(t.get("BUILDIDENTIFIER"))));

        // ## Full Version String
        // A main version, followed optionally by a pre-release version and
        // build metadata.

        // Note that the only major, minor, patch, and pre-release sections of
        // the version string are capturing groups.  The build metadata is not a
        // capturing group, because it should not ever be used in version
        // comparison.

        createToken("FULLPLAIN", String.format("v?%s%s?%s?", src.get(t.get("MAINVERSION")),
                src.get(t.get("PRERELEASE")), src.get(t.get("BUILD"))));

        createToken("FULL", String.format("^%s$", src.get(t.get("FULLPLAIN"))));

        // like full, but allows v1.2.3 and =1.2.3, which people do sometimes.
        // also, 1.0.0alpha1 (prerelease without the hyphen) which is pretty
        // common in the npm registry.
        createToken("LOOSEPLAIN",
                String.format("[v=\\s]*%s%s?%s?", src.get(t.get("MAINVERSIONLOOSE")), src.get(t.get("PRERELEASELOOSE")),
                        src.get(t.get("BUILD"))));

        createToken("GTLT", "((?:<|>)?=?)");

        // Something like "2.*" or "1.2.x".
        // Note that "x.x" is a valid xRange identifer, meaning "any version"
        // Only the first item is strictly required.
        createToken("XRANGEIDENTIFIER", String.format("%s|x|X|\\*", src.get(t.get("NUMERICIDENTIFIER"))));

        createToken("XRANGEPLAIN", String.format("[v=\\s]*(%s)(?:\\.(%s)(?:\\.(%s)(?:%s)?%s?)?)?",
                src.get(t.get("XRANGEIDENTIFIER")), src.get(t.get("XRANGEIDENTIFIER")),
                src.get(t.get("XRANGEIDENTIFIER")), src.get(t.get("PRERELEASE")), src.get(t.get("BUILD"))));


        createToken("XRANGE", String.format("^%s\\s*%s$", src.get(t.get("GTLT")), src.get(t.get("XRANGEPLAIN"))));

        /*
        // Coercion.
        // Extract anything that could conceivably be a part of a valid semver
        createToken("COERCE", `${'(^|[^\\d])' +
                '(\\d{1,'}${MAX_SAFE_COMPONENT_LENGTH}})` +
            `(?:\\.(\\d{1,${MAX_SAFE_COMPONENT_LENGTH}}))?` +
            `(?:\\.(\\d{1,${MAX_SAFE_COMPONENT_LENGTH}}))?` +
            `(?:$|[^\\d])`)
        createToken("COERCERTL", src[t.COERCE], true)
         */

        // Tilde ranges.
        // Meaning is "reasonably at or greater than"
        createToken("LONETILDE", "(?:~>?)");

        createToken("TILDETRIM", String.format("(\\s*)%s\\s+", src.get(t.get("LONETILDE"))), true);

        createToken("TILDE", String.format("^%s%s$", src.get(t.get("LONETILDE")), src.get(t.get("XRANGEPLAIN"))));

        // Caret ranges.
        // Meaning is "at least and backwards compatible with"
        createToken("LONECARET", "(?:\\^)");

        createToken("CARETTRIM", String.format("(\\s*)%s\\s+", src.get(t.get("LONECARET"))), true);


        createToken("CARET", String.format("^%s%s$", src.get(t.get("LONECARET")), src.get(t.get("XRANGEPLAIN"))));

        // A simple gt/lt/eq thing, or just "" to indicate "any version"
        createToken("COMPARATOR", String.format("^%s\\s*(%s)$|^$", src.get(t.get("GTLT")),
                src.get(t.get("FULLPLAIN"))));

        // An expression to strip any whitespace between the gtlt and the thing
        // it modifies, so that `> 1.2.3` ==> `>1.2.3`
        createToken("COMPARATORTRIM", String.format("(\\s*)%s\\s*(%s|%s)",
                        src.get(t.get("GTLT")), src.get(t.get("LOOSEPLAIN")),
                        src.get(t.get("XRANGEPLAIN"))),
                true);

        // Something like `1.2.3 - 1.2.4`
        // Note that these all use the loose form, because they'll be
        // checked against either the strict or loose comparator form
        // later.
        createToken("HYPHENRANGE", String.format("^\\s*(%s)\\s+-\\s+(%s)\\s*$",
                src.get(t.get("XRANGEPLAIN")), src.get(t.get("XRANGEPLAIN"))));

        // Star ranges basically just allow anything at all.
        createToken("STAR", "(<|>)?=?\\s*\\*");
        // >=0.0.0 is like a star
        createToken("GTE0", "^\\s*>=\\s*0\\.0\\.0\\s*$");
        createToken("GTE0PRE", "^\\s*>=\\s*0\\.0\\.0-0\\s*$");
    }

    private static void createToken(String name, String value) {
        createToken(name, value, false);
    }

    private static void createToken(String name, String value, boolean isGlobal) {
        int index = R++;
        t.put(name, index);
        src.add(index, value);
        re.add(index, Pattern.compile(value));
    }
}
