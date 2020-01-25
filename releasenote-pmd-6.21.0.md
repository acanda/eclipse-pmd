# Modified Rules

The Java rule AvoidLiteralsInIfCondition (java-errorprone) has a new property ignoreExpressions. This property is set by default to true in order to maintain compatibility. If this property is set to false, then literals in more complex expressions are considered as well.

The Apex rule ApexCSRF (apex-errorprone) has been moved from category “Security” to “Error Prone”. The Apex runtime already prevents DML statements from being executed, but only at runtime. So, if you try to do this, you’ll get an error at runtime, hence this is error prone. See also the discussion on #2064.

The Java rule CommentRequired (java-documentation) has a new property classCommentRequirement. This replaces the now deprecated property headerCommentRequirement, since the name was misleading. (File) header comments are not checked, but class comments are.

# Fixed Issues

## apex
- #2208: [apex] ASTFormalComment should implement ApexNode<T>
## core
- #1984: [java] Cyclomatic complexity is misreported (lack of clearing metrics cache)
- #2006: [core] PMD should warn about multiple instances of the same rule in a ruleset
- #2161: [core] ResourceLoader is deprecated and marked as internal but is exposed
- #2170: [core] DocumentFile doesn’t preserve newlines
## doc
- #2214: [doc] Link broken in pmd documentation for writing Xpath rules
## java
- #2212: [java] JavaRuleViolation reports wrong class name
## java-bestpractices
- #2149: [java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
## java-codestyle
- #2167: [java] UnnecessaryLocalBeforeReturn false positive with variable captured by method reference
## java-documentation
- #1683: [java] CommentRequired property names are inconsistent
## java-errorprone
- #2140: [java] AvoidLiteralsInIfCondition: false negative for expressions
- #2196: [java] InvalidLogMessageFormat does not detect extra parameters when no placeholders
## java-performance
- #2141: [java] StringInstatiation: False negative with String-array access
## plsql
- #2008: [plsql] In StringLiteral using alternative quoting mechanism single quotes cause parsing errors
- #2009: [plsql] Multiple DDL commands are skipped during parsing

# External Contributions

- #2041: [modelica] Initial implementation for PMD - Anatoly Trosinenko
- #2051: [doc] Update the docs on adding a new language - Anatoly Trosinenko
- #2069: [java] CommentRequired: make property names consistent - snuyanzin
- #2169: [modelica] Follow-up fixes for Modelica language module - Anatoly Trosinenko
- #2193: [core] Fix odd logic in test runner - Egor Bredikhin
- #2194: [java] Fix odd logic in AvoidUsingHardCodedIPRule - Egor Bredikhin
- #2195: [modelica] Normalize invalid node ranges - Anatoly Trosinenko
- #2199: [modelica] Fix Javadoc tags - Anatoly Trosinenko
- #2225: [core] CPD: report endLine / column informations for found duplications - Maikel Steneker
