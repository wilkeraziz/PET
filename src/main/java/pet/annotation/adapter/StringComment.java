package pet.annotation.adapter;

import pet.annotation.Comment;

public class StringComment implements Comment {
	private final String comment;

	public StringComment(final String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return comment;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof StringComment)) {
			return false;
		}
		final StringComment other = (StringComment) o;
		return comment.equals(other.comment);
	}
}
