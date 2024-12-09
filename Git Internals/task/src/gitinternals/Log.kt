package gitinternals

data class Log(
    val hash: String,
    val committer: Committer,
    val message: GitCommitMessage,
    val isMerged: Boolean
) {

  companion object {
    operator fun invoke(content: GitFileContent, isMerged: Boolean = false): Log {
      val commit = content.commit
      return Log(content.hash, commit.meta.committer, commit.message, isMerged)
    }
  }

  override fun toString(): String =
      listOf(
              "Commit: $hash${if(isMerged) " (merged)" else ""}",
              committer.toString(),
              message.joinToString("\n"),
          )
          .joinToString("\n") + "\n"
}
