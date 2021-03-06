package ch.tutteli.atrium.specs.integration

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.internal.expect
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.specs.*
import ch.tutteli.atrium.translations.DescriptionDateTimeLikeAssertion
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import java.time.LocalDate
import java.time.LocalDateTime

abstract class LocalDateTimeFeatureAssertionsSpec(
    yearFeature: Feature0<LocalDateTime, Int>,
    year: Fun1<LocalDateTime, Expect<Int>.() -> Unit>,
    monthFeature: Feature0<LocalDateTime, Int>,
    month: Fun1<LocalDateTime, Expect<Int>.() -> Unit>,
    describePrefix: String = "[Atrium] "
) : Spek({

    include(object : SubjectLessSpec<LocalDateTime>(describePrefix,
        yearFeature.forSubjectLess().adjustName { "$it feature" },
        year.forSubjectLess { isGreaterThan(2000) }
    ) {})

    include(object : AssertionCreatorSpec<LocalDateTime>(
        describePrefix, LocalDateTime.now().withYear(2040),
        year.forAssertionCreatorSpec("$toBeDescr: 1") { toBe(2040) }
    ) {})

    fun describeFun(vararg funName: String, body: Suite.() -> Unit) =
        describeFunTemplate(describePrefix, funName, body = body)

    val fluent = expect(LocalDateTime.now().withMonth(5).withYear(2009))
    val monthDescr = DescriptionDateTimeLikeAssertion.MONTH.getDefault()
    val yearDescr = DescriptionDateTimeLikeAssertion.YEAR.getDefault()

    describeFun("val ${yearFeature.name}") {
        val yearVal = yearFeature.lambda

        context("LocalDateTime with year 2009") {
            it("toBe(2009) holds") {
                fluent.yearVal().toBe(2009)
            }
            it("toBe(2019) fails") {
                expect {
                    fluent.yearVal().toBe(2019)
                }.toThrow<AssertionError> {
                    messageContains("$yearDescr: 2009")
                }
            }
        }
    }

    describeFun("fun ${year.name}") {
        val yearFun = year.lambda

        context("LocalDateTime with year 2009") {
            it("is greater than 2009 holds") {
                fluent.yearFun { isGreaterThan(2008) }
            }
            it("is less than 2009 fails") {
                expect {
                    fluent.yearFun { isLessThan(2009) }
                }.toThrow<AssertionError> {
                    messageContains("$yearDescr: 2009")
                }
            }
        }
    }


    describeFun("val ${monthFeature.name}") {
        val monthVal = monthFeature.lambda

        context("LocalDateTime with month May(5)") {
            it("toBe(May) holds") {
                fluent.monthVal().toBe(5)
            }
            it("toBe(April) fails") {
                expect {
                    fluent.monthVal().toBe(4)
                }.toThrow<AssertionError> {
                    messageContains("$monthDescr: 5" )
                }
            }
        }
    }

    describeFun("fun ${month.name}") {
        val monthFun = month.lambda

        context("LocalDateTime with month March(3)") {
            it("is greater than February(2) holds") {
                fluent.monthFun { isGreaterThan(2) }
            }
            it("is less than 5 fails") {
                expect {
                    fluent.monthFun { isLessThan(5) }
                }.toThrow<AssertionError> {
                    messageContains("$monthDescr: 5")
                }
            }
        }
    }
})
