package gitinternals

data class GitBlob(var blob: GitCommitBlob) {

  companion object {
    operator fun invoke(bytes: GitFileObject): GitBlob =
        GitBlob(String(bytes).lines().dropWhile { it.isEmpty() }.dropLastWhile { it.isEmpty() })
  }

  override fun toString(): String = "*BLOB*\n${blob.joinToString("\n")}"
}
