package com.bsi.md.agent.repository;

import com.bsi.md.agent.entity.AgDataSource;
import org.apache.ibatis.annotations.Delete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @author fish
 */
@Component
public interface AgDataSourceRepository extends JpaRepository<AgDataSource, Long> {
    @Delete("delete from md_agent_datasource where id = ?1")
    void deleteById(String id);
}
