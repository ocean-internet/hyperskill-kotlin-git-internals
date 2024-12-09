package gitinternals

data class GitCommit(val meta: GitCommitMeta, val message: GitCommitMessage) {

  companion object {
    operator fun invoke(bytes: GitFileObject): GitCommit =
        GitCommit(
            GitCommitMeta(bytes),
            String(bytes)
                .lines()
                .dropWhile { it.isNotEmpty() }
                .drop(1)
                .dropLastWhile { it.isEmpty() })
  }

  override fun toString(): String =
      "*COMMIT*\n${meta}\ncommit message:\n${message.joinToString("\n")}"
}
