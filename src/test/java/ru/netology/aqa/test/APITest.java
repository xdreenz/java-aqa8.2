package ru.netology.aqa.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.aqa.data.APIHelper;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.data.SQLHelper;

import static ru.netology.aqa.data.SQLHelper.*;

public class APITest {
    @AfterEach
    void tearDownCodes() {
        cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @Test
    public void validTransfer() {
        var authInfo = DataHelper.getAuthInfoOfTestUser();
        APIHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = SQLHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCode());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);
        var cardsBalances = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var card1Balance = cardsBalances.get(DataHelper.getCard1Info().getId());
        var card2Balance = cardsBalances.get(DataHelper.getCard2Info().getId());
        var amount = DataHelper.generateValidAmountToTransfer(card1Balance);
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getCard1Info().getNumber(),
                DataHelper.getCard2Info().getNumber(), amount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardsBalances = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var actualCard1Balance = cardsBalances.get(DataHelper.getCard1Info().getId());
        var actualCard2Balance = cardsBalances.get(DataHelper.getCard2Info().getId());
        Assertions.assertEquals(card1Balance - amount, actualCard1Balance);
        Assertions.assertEquals(card2Balance + amount, actualCard2Balance);
    }
}