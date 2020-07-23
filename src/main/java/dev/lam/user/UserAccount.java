package dev.lam.user;

import java.util.Set;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import dev.lam.models.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "UserAccount")
@EqualsAndHashCode
public class UserAccount {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int user_id;
	@Column
	private String email;
	@Column
	private String username;
	@Transient
	private String password;
	@Column
	private String first_name;
	@Column
	private String last_name;
	@Column
	private String roles;
	
	@OneToMany(mappedBy = "user")
	private Set<Post> posts;
}
