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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode
public class Comparator {
  public final String value;
  private String operator;
  public SemVer semver;
  public static final AnySemVer ANY = Comparator.AnySemVer.ANY;

  public Comparator(String comp) {
    this.parse(comp);

    if (this.semver == ANY) {
      this.value = "";
    } else {
      this.value = this.operator + this.semver.getVersion();
    }
  }

  private void parse (String comp) {
    Pattern r = SemVerRegexes.re.get(SemVerRegexes.t.get("COMPARATOR"));
    Matcher m = r.matcher(comp);

    if (!m.find()) {
      throw new IllegalArgumentException("Invalid comparator " + comp);
    }

    this.operator = m.group(1) != null ? m.group(1) : "";
    if (this.operator.equals("=")) {
      this.operator = "";
    }

    // if it literally is just '>' or '' then allow anything.
    if (m.group(2) == null) {
      this.semver = ANY;
    } else {
      this.semver = new SemVer(m.group(2));
    }
  }

  public boolean test (Object version) {
    if (this.semver == ANY || version == ANY) {
      return true;
    }

    if (version instanceof String) {
      try {
        version = new SemVer((String) version);
      } catch (RuntimeException e) {
        return false;
      }
    }

    return cmp((SemVer) version, this.operator, this.semver);
  }

  public static boolean cmp(SemVer a, String op, SemVer b) {
    switch (op) {
      case "===":
        return a.getVersion().equals(b.getVersion());
      case "!==":
        return !a.getVersion().equals(b.getVersion());
      case "":
      case "=":
      case "==":
        return eq(a, b);
      case "!=":
        return neq(a, b);
      case ">":
        return gt(a, b);
      case ">=":
        return gte(a, b);
      case "<":
        return lt(a, b);
      case "<=":
        return lte(a, b);
      default:
        throw new IllegalArgumentException("Invalid operator " + op);
    }
  }

  public static boolean eq(SemVer a, SemVer b){
    return a.compareTo(b) == 0;
  }

  public static boolean neq(SemVer a, SemVer b){
    return a.compareTo(b) != 0;
  }

  public static boolean gt(SemVer a, SemVer b){
    return a.compareTo(b) > 0;
  }

  public static boolean gte(SemVer a, SemVer b){
    return a.compareTo(b) >= 0;
  }

  public static boolean lt(SemVer a, SemVer b){
    return a.compareTo(b) < 0;
  }

  public static boolean lte(SemVer a, SemVer b){
    return a.compareTo(b) <= 0;
  }

  public boolean intersects (Comparator comp) {
    if (this.operator.equals("")) {
      if (this.value.equals("")) {
        return true;
      }
      return new Range(comp.value).test(this.semver);
    } else if (comp.operator.equals("")) {
      if (comp.value.equals("")) {
        return true;
      }
      return new Range(this.value).test(comp.semver);
    }

    boolean sameDirectionIncreasing =
      (this.operator.equals(">=") || this.operator.equals(">")) &&
      (comp.operator.equals(">=") || comp.operator.equals(">"));
    boolean sameDirectionDecreasing =
      (this.operator.equals("<=") || this.operator.equals("<")) &&
      (comp.operator.equals("<=") || comp.operator.equals("<"));
    boolean sameSemVer = Objects.equals(this.semver.getVersion(), comp.semver.getVersion());
    boolean differentDirectionsInclusive =
      (this.operator.equals(">=") || this.operator.equals("<=")) &&
      (comp.operator.equals(">=") || comp.operator.equals("<="));
    boolean oppositeDirectionsLessThan =
      cmp(this.semver, "<", comp.semver) &&
      (Objects.equals(this.operator, ">=") || Objects.equals(this.operator, ">")) &&
        (Objects.equals(comp.operator, "<=") || Objects.equals(comp.operator, "<"));
    boolean oppositeDirectionsGreaterThan =
      cmp(this.semver, ">", comp.semver) &&
      (Objects.equals(this.operator, "<=") || Objects.equals(this.operator, "<")) &&
        (Objects.equals(comp.operator, ">=") || Objects.equals(comp.operator, ">"));

    return (
      sameDirectionIncreasing ||
      sameDirectionDecreasing ||
      (sameSemVer && differentDirectionsInclusive) ||
      oppositeDirectionsLessThan ||
      oppositeDirectionsGreaterThan
    );
  }

  @Override
  public String toString() {
    return this.value;
  }

  public static class AnySemVer extends SemVer {
    final static AnySemVer ANY = new AnySemVer();
    private AnySemVer() {
      super("0.0.0");
    }
  }
}

