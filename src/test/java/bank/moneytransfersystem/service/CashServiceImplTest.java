//package bank.moneytransfersystem.service;
//
//import bank.moneytransfersystem.entities.Cash;
//import bank.moneytransfersystem.exception.AlreadyExistsException;
//import bank.moneytransfersystem.exception.IllegalArgumentException;
//import bank.moneytransfersystem.exception.NotFoundException;
//import bank.moneytransfersystem.repo.CashRepository;
//import bank.moneytransfersystem.service.impl.CashServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CashServiceImplTest {
//
//    @Mock
//    private CashRepository cashRepository;
//
//    @InjectMocks
//    private CashServiceImpl cashService;
//
//    @Test
//    void saveCash_ShouldThrowException_WhenCashIsNull() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            cashService.saveCash(null);
//        });
//        assertEquals("Cash object must not be null", exception.getMessage());
//    }
//
//    @Test
//    void saveCash_ShouldThrowAlreadyExistsException_WhenCashExists() {
//        Cash cash = new Cash();
//        cash.setCashName("Cash1");
//
//        when(cashRepository.existsByCashName(cash.getCashName())).thenReturn(true);
//
//        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
//            cashService.saveCash(cash);
//        });
//        assertEquals("Cash with name 'Cash1' already exists", exception.getMessage());
//    }
//
//    @Test
//    void saveCash_ShouldSaveCashWithMinBalance_WhenBalanceIsLessThan1000() throws AlreadyExistsException, IllegalArgumentException {
//        Cash cash = new Cash();
//        cash.setCashName("Cash1");
//        cash.setBalance(500);
//
//        when(cashRepository.existsByCashName(cash.getCashName())).thenReturn(false);
//
//        cashService.saveCash(cash);
//
//        assertEquals(1000.0, cash.getBalance());
//        verify(cashRepository, times(1)).save(cash);
//    }
//
//    @Test
//    void updateCashById_ShouldThrowNotFoundException_WhenCashNotFound() {
//        when(cashRepository.findById(1L)).thenReturn(Optional.empty());
//
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
//            cashService.updateCashById(1L, new Cash());
//        });
//
//        assertEquals("Cash with ID 1 not found", exception.getMessage());
//    }
//
//    @Test
//    void updateCashById_ShouldUpdateCash_WhenFound() throws NotFoundException {
//        Cash cash = new Cash();
//        cash.setCashName("Old Cash");
//        cash.setBalance(1000.0);
//
//        when(cashRepository.findById(1L)).thenReturn(Optional.of(cash));
//
//        Cash newCash = new Cash();
//        newCash.setCashName("New Cash");
//        newCash.setBalance(2000.0);
//
//        cashService.updateCashById(1L, newCash);
//
//        assertEquals("New Cash", cash.getCashName());
//        assertEquals(2000.0, cash.getBalance());
//        verify(cashRepository, times(1)).save(cash);
//    }
//
//    @Test
//    void findAllCash_ShouldReturnCashList() {
//        List<Cash> cashList = Arrays.asList(new Cash(), new Cash());
//        when(cashRepository.findAll()).thenReturn(cashList);
//
//        List<Cash> result = cashService.findAllCash();
//        assertEquals(2, result.size());
//        verify(cashRepository, times(1)).findAll();
//    }
//
//    @Test
//    void removeCash_ShouldThrowNotFoundException_WhenCashNotFound() {
//        when(cashRepository.findById(1L)).thenReturn(Optional.empty());
//
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
//            cashService.removeCash(1L);
//        });
//
//        assertEquals("Cash with ID 1 not found", exception.getMessage());
//    }
//
//    @Test
//    void removeCash_ShouldRemoveCash_WhenFound() throws NotFoundException {
//        Cash cash = new Cash();
//        when(cashRepository.findById(1L)).thenReturn(Optional.of(cash));
//
//        cashService.removeCash(1L);
//
//        verify(cashRepository, times(1)).delete(cash);
//    }
//}
//
