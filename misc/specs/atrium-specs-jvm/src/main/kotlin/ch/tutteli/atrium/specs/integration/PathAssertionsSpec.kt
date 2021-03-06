package ch.tutteli.atrium.specs.integration

import ch.tutteli.atrium.api.fluent.en_GB.messageContains
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.internal.expect
import ch.tutteli.atrium.specs.*
import ch.tutteli.atrium.translations.DescriptionBasic.NOT_TO
import ch.tutteli.atrium.translations.DescriptionBasic.TO
import ch.tutteli.atrium.translations.DescriptionPathAssertion
import ch.tutteli.spek.extensions.TempFolder
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import java.nio.file.Path
import java.nio.file.Paths

abstract class PathAssertionsSpec(
    exists: Fun0<Path>,
    existsNot: Fun0<Path>,
    startsWith: Fun1<Path, Path>,
    startsNotWith: Fun1<Path, Path>,
    endsWith: Fun1<Path, Path>,
    endsNotWith: Fun1<Path, Path>,
    describePrefix: String = "[Atrium] "
) : Spek({

    val tempFolder = TempFolder.perTest() //or perGroup()
    registerListener(tempFolder)

    include(object : SubjectLessSpec<Path>(
        "$describePrefix[Path] ",
        exists.forSubjectLess()
    ) {})

    fun describeFun(vararg funName: String, body: Suite.() -> Unit) =
        describeFunTemplate(describePrefix, funName, body = body)

    describeFun(exists.name, existsNot.name) {
        val existsFun = exists.lambda
        val existsNotFun = existsNot.lambda
        context("file exists") {
            it("${exists.name} does not throw") {
                val file = tempFolder.newFile("test")
                expect(file).existsFun()
            }

            it("${existsNot.name} throws an AssertionError") {
                val file = tempFolder.newFile("exists-though-shouldnt")
                expect {
                    expect(file).existsNotFun()
                }.toThrow<AssertionError> {
                    messageContains("${NOT_TO.getDefault()}: ${DescriptionPathAssertion.EXIST.getDefault()}")
                }
            }
        }

        context("folder exists") {
            it("${exists.name} does not throw") {
                val file = tempFolder.newFolder("test")
                expect(file).existsFun()
            }


            it("${existsNot.name} throws an AssertionError") {
                val folder = tempFolder.newFolder("exists-though-shouldnt")
                expect {
                    expect(folder).existsNotFun()
                }.toThrow<AssertionError> {
                    messageContains("${NOT_TO.getDefault()}: ${DescriptionPathAssertion.EXIST.getDefault()}")
                }
            }
        }

        context("file does not exist") {
            it("${exists.name} throws an AssertionError") {
                expect {
                    expect(Paths.get("nonExistingFile")).existsFun()
                }.toThrow<AssertionError> {
                    messageContains(
                        "${TO.getDefault()}: ${DescriptionPathAssertion.EXIST.getDefault()}"
                    )
                }
            }

            it("${existsNot.name} does not throw") {
                expect(Paths.get("nonExistingFile")).existsNotFun()
            }
        }
    }

    describeFun(startsWith.name, startsNotWith.name, endsWith.name, endsNotWith.name) {
        val startsWithFun = startsWith.lambda
        val startsNotWithFun = startsNotWith.lambda
        val endsWithFun = endsWith.lambda
        val endsNotWithFun = endsNotWith.lambda

        val expectPath = "/some/path/for/test"
        context("path '$expectPath'") {
            it("${startsWith.name} '/some/path/' does not throw") {
                expect(Paths.get(expectPath))
                    .startsWithFun(Paths.get("/some/path/"))
            }

            it("${startsWith.name} '/other/path' throws an AssertionError") {
                expect {
                    expect(Paths.get(expectPath))
                        .startsWithFun(Paths.get("/other/path"))
                }.toThrow<AssertionError> {
                    messageContains("${DescriptionPathAssertion.STARTS_WITH.getDefault()}:")
                }
            }

            it("${startsNotWith.name} '/other/path' does not throw") {
                expect(Paths.get(expectPath))
                    .startsNotWithFun(Paths.get("/other/path"))
            }

            it("${startsNotWith.name} '/some/pa' does not match partials") {
                expect(Paths.get(expectPath))
                    .startsNotWithFun(Paths.get("/some/pa"))
            }

            it("${startsNotWith.name} '/some/path' throws an AssertionError") {
                expect {
                    expect(Paths.get(expectPath))
                        .startsNotWithFun(Paths.get("/some/path"))
                }.toThrow<AssertionError> {
                    messageContains("${DescriptionPathAssertion.STARTS_NOT_WITH.getDefault()}:")
                }
            }

            it("${endsWith.name} 'for/test' does not throw") {
                expect(Paths.get(expectPath))
                    .endsWithFun(Paths.get("for/test"))
            }

            it("${endsWith.name} 'for/another' throws an AssertionError") {
                expect {
                    expect(Paths.get(expectPath))
                        .endsWithFun(Paths.get("for/another"))
                }.toThrow<AssertionError> {
                    messageContains("${DescriptionPathAssertion.ENDS_WITH.getDefault()}:")
                }
            }

            it("${endsNotWith.name} 'for/test' throws an AssertionError") {
                expect {
                    expect(Paths.get(expectPath))
                        .endsNotWithFun(Paths.get("for/test"))
                }.toThrow<AssertionError> {
                    messageContains("${DescriptionPathAssertion.ENDS_NOT_WITH.getDefault()}:")
                }
            }

            it("${endsNotWith.name} 'for/another' does not throw") {
                expect(Paths.get(expectPath))
                    .endsNotWithFun(Paths.get("for/another"))
            }
        }
    }
})
