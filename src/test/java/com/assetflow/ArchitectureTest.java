package com.assetflow;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.assetflow", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_web_or_persistence =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..api..", "..infrastructure..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domain_should_not_depend_on_spring_web =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework.web..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_should_be_suffixed_and_in_api_package =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..api..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_should_not_be_depended_on_by_domain_or_application =
            noClasses().that().resideInAnyPackage("..domain..", "..application..")
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("Controller")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule use_cases_should_reside_in_application_package =
            classes().that().haveSimpleNameEndingWith("UseCase")
                    .should().resideInAPackage("..application..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repositories_should_be_interfaces_or_in_infrastructure =
            classes().that().haveSimpleNameEndingWith("Repository")
                    .should().resideInAnyPackage("..domain..", "..infrastructure..")
                    .allowEmptyShould(true);
}
