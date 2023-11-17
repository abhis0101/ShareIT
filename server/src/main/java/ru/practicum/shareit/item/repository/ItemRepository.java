package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it " +
            "from Item as it " +
            "where it.available = true " +
            "and (lower(it.description) like lower(concat('%', ?1, '%')) " +
            "or lower(it.name) like lower(concat('%', ?1, '%')))")
    Page<Item> search(String text, Pageable page);

    Page<Item> findAllByOwnerIdOrderById(Long userId, Pageable page);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIn(List<ItemRequest> requests);
}
