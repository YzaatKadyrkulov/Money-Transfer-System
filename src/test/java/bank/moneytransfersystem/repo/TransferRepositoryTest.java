package bank.moneytransfersystem.repo;

import bank.moneytransfersystem.entities.Cash;
import bank.moneytransfersystem.entities.Transfer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TransferRepositoryTest {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private CashRepository cashRepository;

    @Test
    void findByCode_ShouldReturnTransfer_WhenCodeExists() {
        Transfer transfer = new Transfer();
        transfer.setCode("uniqueCode");
        transfer.setCreatedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        Optional<Transfer> result = transferRepository.findByCode("uniqueCode");

        assertTrue(result.isPresent());
        assertEquals("uniqueCode", result.get().getCode());
    }

    @Test
    void findByDateRange_ShouldReturnTransfers_WhenInDateRange() {
        Transfer transfer1 = new Transfer();
        transfer1.setCreatedAt(LocalDateTime.now().minusDays(5));

        Transfer transfer2 = new Transfer();
        transfer2.setCreatedAt(LocalDateTime.now().minusDays(3));

        transferRepository.save(transfer1);
        transferRepository.save(transfer2);

        List<Transfer> result = transferRepository.findByDateRange(
                LocalDateTime.now().minusDays(7), LocalDateTime.now());

        assertEquals(2, result.size());
    }

    @Test
    void findAllTransfers_ShouldReturnTransfers_WhenCashIdMatches() {
        Cash cash = new Cash();
        cash = cashRepository.save(cash);

        Transfer transfer = new Transfer();
        transfer.setFromCash(cash);
        transfer.setCreatedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        List<Transfer> result = transferRepository.findAllTransfers(cash.getId());
        assertEquals(1, result.size());
        assertEquals(cash.getId(), result.get(0).getFromCash().getId());
    }

    @Test
    void findTransfersByCriteria_ShouldReturnTransfers_WhenCriteriaMatch() {
        Cash cash = new Cash();
        cash = cashRepository.save(cash);

        Transfer transfer1 = new Transfer();
        transfer1.setFromCash(cash);
        transfer1.setCreatedAt(LocalDateTime.now().minusDays(5));
        transfer1.setComment("Important transfer");
        transferRepository.save(transfer1);

        Transfer transfer2 = new Transfer();
        transfer2.setFromCash(cash);
        transfer2.setCreatedAt(LocalDateTime.now().minusDays(2));
        transfer2.setComment("Routine transfer");
        transferRepository.save(transfer2);

        List<Transfer> result = transferRepository.findTransfersByCriteria(
                LocalDateTime.now().minusDays(7), LocalDateTime.now(), "Important");

        assertEquals(1, result.size());
        assertEquals("Important transfer", result.get(0).getComment());
    }
}