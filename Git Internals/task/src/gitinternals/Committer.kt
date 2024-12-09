package gitinternals

data class Committer(
    override val name: String,
    override val email: String,
    override val timestamp: String
) : Contributor(name, email, timestamp) {
  companion object {
    operator fun invoke(string: String): Committer = fromString(string) as Committer
  }

  override fun toString(): String = "$name $email commit timestamp: $timestamp"
}
