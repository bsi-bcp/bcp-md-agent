package com.bsi.md.agent.repository;

import com.bsi.md.agent.entity.AgConfig;
import org.apache.ibatis.annotations.Delete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @author fish
 */
@Component
public interface AgConfigRepository extends JpaRepository<AgConfig, Long> {
    @Delete("delete from md_agent_config where id = ?1")
    void deleteById(String id);
}
