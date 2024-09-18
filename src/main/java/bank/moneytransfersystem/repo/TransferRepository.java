package bank.moneytransfersystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import bank.moneytransfersystem.entities.Transfer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByCode(String code);

    @Query("SELECT t FROM Transfer t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Transfer> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transfer t WHERE t.fromCash.id = :cashId or t.toCash.id = :cashId")
    List<Transfer> findAllTransfers(@Param("cashId") Long cashId);

    @Query("SELECT t FROM Transfer t WHERE t.createdAt BETWEEN :fromDate AND :toDate AND (:comment IS NULL OR t.comment LIKE %:comment%)")
    List<Transfer> findTransfersByCriteria(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("comment") String comment);
}
