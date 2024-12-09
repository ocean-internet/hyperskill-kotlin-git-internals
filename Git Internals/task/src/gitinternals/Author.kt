package gitinternals

data class Author(
    override val name: String,
    override val email: String,
    override val timestamp: String
) : Contributor(name, email, timestamp) {
  companion object {
    operator fun invoke(string: String): Author = fromString(string) as Author
  }

  override fun toString(): String = "$name $email original timestamp: $timestamp"
}
