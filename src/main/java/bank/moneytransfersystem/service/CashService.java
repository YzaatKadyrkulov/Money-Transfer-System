package bank.moneytransfersystem.service;

import bank.moneytransfersystem.entities.Cash;
import bank.moneytransfersystem.exception.AlreadyExistsException;
import bank.moneytransfersystem.exception.IllegalArgumentException;
import bank.moneytransfersystem.exception.NotFoundException;

import java.util.List;

public interface CashService {
    void saveCash(Cash cash) throws AlreadyExistsException, IllegalArgumentException;

    void updateCashById(Long cashId, Cash cash) throws NotFoundException;

    Cash getCashById(Long id) throws NotFoundException;

    List<Cash> findAllCash();

    List<Cash> findByCashName(String name);

    void removeCash(Long id) throws NotFoundException;
}
