package gitinternals

import java.io.File

fun main() {
  val gitDirectory: GitDirectory = getInput("Enter .git directory location:")
  val command = getInput("Enter command:")

  when (command) {
    "cat-file" -> gitDirectory.catFile()
    "list-branches" -> gitDirectory.listBranches()
    "log" -> gitDirectory.log()
    "commit-tree" -> gitDirectory.commitTree()
    else -> throw Error("Invalid command $command")
  }
}

fun GitDirectory.catFile(): Unit =
    getInput("Enter git object hash:").let { GitFileContent(it, this) }.let(::println)

typealias GitBranch = String // refs/heads/{name}

val GitBranch.name: String
  get() = substringAfter("refs/heads/")

fun GitDirectory.listBranches() {
  val head: GitBranch = this.head
  val entries: List<GitBranch> = this.branches

  println(
      entries.filter { it != head }.map { it.name }.joinToString("\n") { "  $it" } +
          "\n* ${head.name}")
}

fun GitDirectory.log() {
  val name = getInput("Enter branch name:")
  val filename = "$this/refs/heads/$name"

  var gitHash: GitHash? = File(filename).readText().trim()

  if (gitHash == null) return

  println(Log(GitFileContent(gitHash, this)))

  while (gitHash != null) {

    val content = GitFileContent(gitHash, this)
    val commit = content.commit
    var parents = commit.meta.parents

    parents.reversed().forEachIndexed { index, parent ->
      printLog(parent, parents.size > 1 && index == 0)
    }

    gitHash = parents.firstOrNull()
  }
}

fun GitDirectory.commitTree() {
  val gitHash = getInput("Enter commit-hash:")

  val content = GitFileContent(gitHash, this)
  val commit = content.commit

  printTree(commit.meta.tree)
}

fun GitDirectory.printLog(gitHash: GitHash, isMerged: Boolean = false) {
  println(Log(GitFileContent(gitHash, this), isMerged))
}

fun GitDirectory.printTree(treeHash: GitHash, prefix: String = "") {
  val tree = GitFileContent(treeHash, this).tree
  tree.lines.forEach { line ->
    val type = GitFileContent(line.hash, this).type
    when (type) {
      GitFileType.BLOB -> println("${prefix}${line.filename}")
      GitFileType.TREE -> printTree(line.hash, "$prefix${line.filename}/")
      else -> throw IllegalArgumentException()
    }
  }
}
