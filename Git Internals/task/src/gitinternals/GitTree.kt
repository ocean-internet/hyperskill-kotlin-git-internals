package gitinternals

private const val HASH_BYTES = 20

data class GitTree(val lines: List<TreeFileLine>) {

  companion object {
    operator fun invoke(bytes: GitFileObject): GitTree {
      val lines = mutableListOf<TreeFileLine>()
      var data = bytes

      while (data.isNotEmpty()) {
        val nullIndex = data.indexOfNullByte
        val line = data.copyOfRange(0, (nullIndex + HASH_BYTES + 1).coerceAtMost(data.size))
        lines.add(TreeFileLine(line))
        data = data.copyOfRange(line.size, data.size)
      }

      return GitTree(lines)
    }
  }

  override fun toString(): String = "*TREE*\n${lines.joinToString("\n") { it.toString() }}"
}
