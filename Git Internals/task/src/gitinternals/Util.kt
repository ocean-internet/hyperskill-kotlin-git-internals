package gitinternals

import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries

typealias GitHash = String

typealias GitDirectory = String

typealias GitFileObject = ByteArray

internal const val NULL = '\u0000'

private const val DIR_CHARS = 2

fun getInput(prompt: String): String {
  println(prompt)
  return Scanner(System.`in`).nextLine().trim()
}

val ByteArray.indexOfNullByte: Int
  get() = indexOf(NULL.code.toByte())

val Byte.hexString: String
  get() = "%02x".format(this)

val GitDirectory.head: GitBranch
  get() = File("$this/HEAD").readText().trim().split(": ").last()

val GitDirectory.branches: List<GitBranch>
  get() =
      Path("$this/refs/heads").listDirectoryEntries().map { it.toString().substringAfter("$this/") }

fun GitHash.filename(gitDirectory: GitDirectory): String =
    "$gitDirectory/objects/${take(DIR_CHARS)}/${drop(DIR_CHARS)}"

fun GitHash.toRawBytes(gitDirectory: GitDirectory): ByteArray =
    FileInputStream(filename(gitDirectory)).use { InflaterInputStream(it).readAllBytes() }
