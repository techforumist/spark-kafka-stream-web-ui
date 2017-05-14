package org.techforumist.ui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.techforumist.ui.domain.File;

/**
 * @author Sarath Muraleedharan
 *
 */
public interface FileRepository extends JpaRepository<File, Long>{

}
