package gitinternals

import javax.naming.directory.InvalidAttributesException

typealias GitFileHeader = Pair<GitFileType, GitSize>

typealias GitSize = Int

typealias GitCommitBlob = List<String>

typealias GitCommitMessage = List<String>

data class GitFileContent(
    val hash: String,
    val header: GitFileHeader,
    val gitFileObject: GitFileObject
) {
  companion object {

    operator fun invoke(hash: GitHash, directory: GitDirectory): GitFileContent {
      return GitFileContent(hash, hash.toRawBytes(directory))
    }

    operator fun invoke(hash: String, raw: ByteArray): GitFileContent {
      val (type, contentSize) =
          String(raw.copyOfRange(0, raw.indexOfNullByte)).split(" ", limit = 2)
      val gitFileObject = raw.copyOfRange(raw.indexOfNullByte + 1, raw.size)
      return GitFileContent(
          hash,
          GitFileHeader(GitFileType.valueOf(type.uppercase()), contentSize.toInt()),
          gitFileObject)
    }
  }

  val type: GitFileType
    get() = header.first

  val blob: GitBlob by lazy {
    if (type == GitFileType.BLOB) GitBlob(gitFileObject) else throw InvalidAttributesException()
  }
  val commit: GitCommit by lazy {
    if (type == GitFileType.COMMIT) GitCommit(gitFileObject) else throw InvalidAttributesException()
  }
  val tree: GitTree by lazy {
    if (type == GitFileType.TREE) GitTree(gitFileObject) else throw InvalidAttributesException()
  }

  override fun toString(): String =
      when (type) {
        GitFileType.BLOB -> blob
        GitFileType.COMMIT -> commit
        GitFileType.TREE -> tree
      }.toString()
}
