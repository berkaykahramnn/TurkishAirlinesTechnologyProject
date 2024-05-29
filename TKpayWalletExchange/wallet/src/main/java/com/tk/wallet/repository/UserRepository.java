// UserRepository.java
package com.tk.wallet.repository;

import com.tk.wallet.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

}
