# Java 14 Support

This release of PMD brings support for Java 14. PMD can parse Switch Expressions, which have been promoted to be a standard language feature of Java.

PMD also parses Text Blocks as String literals, which is still a preview language feature in Java 14.

The new Pattern Matching for instanceof can be used as well as Records.

# Apex Suppressions

In addition to suppressing violation with the @SuppressWarnings annotation, Apex now also supports the suppressions with a NOPMD comment.

# XPath Rules

As of PMD version 6.22.0, XPath versions 1.0 and the 1.0 compatibility mode are deprecated. XPath 2.0 is superior in many ways, for example for its support for type checking, sequence values, or quantified expressions.

# New Rules

The Rule CognitiveComplexity (apex-design) finds methods and classes that are highly complex and therefore difficult to read and more costly to maintain. In contrast to cyclomatic complexity, this rule uses “Cognitive Complexity”, which is a measure of how difficult it is for humans to read and understand a method.

The Rule TestMethodsMustBeInTestClasses (apex-errorprone) finds test methods that are not residing in a test class. The test methods should be moved to a proper test class. Support for tests inside functional classes was removed in Spring-13 (API Version 27.0), making classes that violate this rule fail compile-time. This rule is however useful when dealing with legacy code.

# Fixed Issues

## apex
- #1087: [apex] Support suppression via //NOPMD
- #2306: [apex] Switch statements are not parsed/supported

## apex-design
- #2162: [apex] Cognitive Complexity rule

## apex-errorprone
- #639: [apex] Test methods should not be in classes other than test classes

## cs
- #2139: [cs] CPD doesn’t understand alternate using statement syntax with C# 8.0

## doc
- #2274: [doc] Java API documentation for PMD

## java
- #2159: [java] Prepare for JDK 14
- #2268: [java] Improve TypeHelper resilience

## java-bestpractices
- #2277: [java] FP in UnusedImports for ambiguous static on-demand imports

## java-design
- #911: [java] UselessOverridingMethod false positive when elevating access modifier

## java-errorprone
- #2242: [java] False-positive MisplacedNullCheck reported
- #2250: [java] InvalidLogMessageFormat flags logging calls using a slf4j-Marker
- #2255: [java] InvalidLogMessageFormat false-positive for a lambda argument

## java-performance
- #2275: [java] AppendCharacterWithChar flags literals in an expression

## plsql
- #2325: [plsql] NullPointerException while running parsing test for CREATE TRIGGER
- #2327: [plsql] Parsing of WHERE CURRENT OF
- #2328: [plsql] Support XMLROOT
- #2331: [plsql] Fix in Comment statement
- #2332: [plsql] Fixed Execute Immediate statement parsing
- #2340: [plsql] Fixed parsing / as divide or execute

# External Contributions

- #2251: [java] FP for InvalidLogMessageFormat when using slf4j-Markers - Kris Scheibe
- #2253: [modelica] Remove duplicated dependencies - Piotrek Żygieło
- #2256: [doc] Corrected XML attributes in release notes - Maikel Steneker
- #2276: [java] AppendCharacterWithCharRule ignore literals in expressions - Kris Scheibe
- #2278: [java] fix UnusedImports rule for ambiguous static on-demand imports - Kris Scheibe
- #2279: [apex] Add support for suppressing violations using the // NOPMD comment - Gwilym Kuiper
- #2280: [cs] CPD: Replace C# tokenizer by an Antlr-based one - Maikel Steneker
- #2297: [apex] Cognitive complexity metrics - Gwilym Kuiper
- #2317: [apex] New Rule - Test Methods Must Be In Test Classes - Brian Nørremark
- #2321: [apex] Support switch statements correctly in Cognitive Complexity - Gwilym Kuiper
- #2326: [plsql] Added XML functions to parser: extract(xml), xml_root and fixed xml_forest - Piotr Szymanski
- #2327: [plsql] Parsing of WHERE CURRENT OF added - Piotr Szymanski
- #2331: [plsql] Fix in Comment statement - Piotr Szymanski
- #2332: [plsql] Fixed Execute Immediate statement parsing - Piotr Szymanski
- #2338: [cs] CPD: fixes in filtering of using directives - Maikel Steneker
- #2339: [cs] CPD: Fixed CPD –ignore-usings option - Maikel Steneker
- #2340: [plsql] fix for parsing / as divide or execute - Piotr Szymanski
- #2342: [xml] Update property used in example - Piotrek Żygieło
- #2344: [doc] Update ruleset examples for ant - Piotrek Żygieło
- #2343: [ci] Disable checking for snapshots in jcenter - Piotrek Żygieło
