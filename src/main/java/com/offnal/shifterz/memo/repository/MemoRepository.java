package com.offnal.shifterz.memo.repository;

import com.offnal.shifterz.memo.domain.Memo;
import com.offnal.shifterz.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
}
