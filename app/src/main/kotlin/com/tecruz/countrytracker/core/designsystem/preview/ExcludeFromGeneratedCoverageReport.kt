package com.tecruz.countrytracker.core.designsystem.preview

/**
 * Marks a function or class to be excluded from JaCoCo coverage reports.
 *
 * JaCoCo (0.8.2+) automatically excludes any method or class annotated with
 * an annotation whose simple name contains "Generated". This annotation
 * leverages that behaviour to keep Compose `@Preview` functions out of
 * coverage metrics without moving them to separate files.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class ExcludeFromGeneratedCoverageReport
