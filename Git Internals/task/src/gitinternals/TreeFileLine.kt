package gitinternals

data class TreeFileLine(val permission: String, val hash: GitHash, val filename: String) {

  companion object {
    operator fun invoke(bytes: ByteArray): TreeFileLine {
      val (permission, filename) =
          String(bytes.copyOfRange(0, bytes.indexOfNullByte)).split(" ", limit = 2)
      val hash =
          bytes.copyOfRange(bytes.indexOfNullByte + 1, bytes.size).joinToString("") { it.hexString }

      return TreeFileLine(permission, hash, filename)
    }
  }

  override fun toString(): String = "$permission $hash $filename"
}
