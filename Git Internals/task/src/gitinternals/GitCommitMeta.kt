package gitinternals

import gitinternals.Contributor.Companion.AUTHOR
import gitinternals.Contributor.Companion.COMMITTER

data class GitCommitMeta(
    val tree: String,
    val parents: List<String>,
    val author: Author,
    val committer: Committer
) {
  companion object {
    const val TREE = "tree"
    const val PARENT = "parent"

    operator fun invoke(gitObject: ByteArray): GitCommitMeta {
      val lines = String(gitObject).lines().takeWhile { it.isNotEmpty() }.map { it.toString() }
      val tree = lines.firstOrNull { it.startsWith("$TREE ") }?.removePrefix("$TREE ").orEmpty()
      val parents = lines.filter { it.startsWith("$PARENT ") }.map { it.removePrefix("$PARENT ") }
      val author = Author(lines.firstOrNull { it.startsWith("$AUTHOR ") }!!)
      val committer = Committer(lines.firstOrNull { it.startsWith("$COMMITTER ") }!!)

      return GitCommitMeta(tree, parents, author, committer)
    }
  }

  override fun toString(): String =
      listOf(
              "$TREE: $tree",
              "${PARENT}s: ${parents.joinToString(" | ")}",
              "$AUTHOR: $author",
              "$COMMITTER: $committer",
          )
          .filter { it.replace("${PARENT}s: ", "").isNotEmpty() }
          .joinToString("\n")
}
