package gitinternals

import junit.framework.TestCase.assertEquals
import org.junit.Test

class GitHashTest {

  private val data =
      listOf(
          mapOf(
              "type" to "blob",
              "permission" to "100644",
              "filename" to "main.kt",
              "content" to "Some file content",
              "hash" to "6edb96760d8fc5be0a86e0ef899e083ad116d53b",
          ),
          mapOf(
              "type" to "blob",
              "permission" to "100644",
              "filename" to "readme.txt",
              "content" to "Some readme content",
              "hash" to "c96726608e9ee896424e1d4aaeac2c5c3d0f8e6e",
          ),
          mapOf(
              "type" to "tree",
              "permission" to "40000",
              "filename" to "some-folder",
              "content" to "Some folder content",
              "hash" to "4be39cc05e8ac2f49935112c86ddbda6bcf0a4e7",
          ),
      )

  @Test
  fun `it should return git filename`() {
    val gitDirectory: GitDirectory = "/home/my_project/.git"
    val gitHash: GitHash = "0eee6a98471a350b2c2316313114185ecaf82f0e"
    val expected = "$gitDirectory/objects/0e/ee6a98471a350b2c2316313114185ecaf82f0e"

    assertEquals(expected, gitHash.filename(gitDirectory))
  }

  @Test
  fun `It should get formatted line`() {
    val content = GitFileContent("", getTreeFileString())
    val tree = content.tree

    assertEquals(
        getGitObjectHexStrings().joinToString("\n") { String(it) },
        tree.lines.joinToString("\n") { "$it" })
  }

  private fun getGitObjectRawStrings(): List<ByteArray> =
      data.map {
        "${it["permission"]} ${it["filename"]}${NULL}".toByteArray() + getGitObjectRawHash(it)
      }

  private fun getGitObjectHexStrings(): List<ByteArray> =
      data.map {
        "${it["permission"]} ${String(getGitObjectHexHash(it))} ${it["filename"]}".toByteArray()
      }

  private fun getGitObjectRawHash(data: Map<String, String>): ByteArray =
      data["hash"]!!.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

  private fun getGitObjectHexHash(data: Map<String, String>): ByteArray =
      data["hash"]!!.toByteArray()

  private fun getTreeFileString() =
      getGitObjectRawStrings().let {
        var result = byteArrayOf()

        it.forEach { result = result + it }

        "tree ${result.size}${NULL}".toByteArray() + result
      }
}
