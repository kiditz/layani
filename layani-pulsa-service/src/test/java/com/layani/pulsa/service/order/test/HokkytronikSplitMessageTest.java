package com.layani.pulsa.service.order.test;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.slerp.core.Domain;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
public class HokkytronikSplitMessageTest {
    @Test
    public void test(){
        Domain payload = new Domain("{\n" +
                "  \"tgl_proses\" : \"2019-01-15 20:41:34\",\n" +
                "  \"tagihan\" : \"124114\",\n" +
                "  \"harga\" : \"0\",\n" +
                "  \"tgl\" : \"2019-01-15 20:41:28\",\n" +
                "  \"idtrx\" : \"132823\",\n" +
                "  \"catatan\" : \"RIFKY ADITYA BASTARA/900/R1/RP124114/1BLN/201901/MET222-438/ADM2750. Tagihan sdh termasuk admin.\\n\",\n" +
                "  \"saldo\" : \"69149\",\n" +
                "  \"ref_idtrx\" : \"0\",\n" +
                "  \"kode_produk\" : \"HCEKPLN\",\n" +
                "  \"tujuan\" : \"546302769285\",\n" +
                "  \"status\" : \"Sukses\"\n" +
                "}\n");
        String note = payload.getString("catatan");
        note = note.substring(0, note.indexOf("."));
        Double postpaidAmount = payload.getDouble("tagihan");
        String[] splitNote = note.split("/");
        String customerNo = splitNote[0];
        Long numOfTrx = Long.valueOf(splitNote[4].replaceAll("[^0-9]", ""));
        String postPaidMonth = splitNote[5];
        Double admCost = Double.parseDouble(splitNote[splitNote.length - 1].replaceAll("[^0-9]", ""));
        assertThat(customerNo, equalTo("RIFKY ADITYA BASTARA"));
        assertThat(postpaidAmount, equalTo(124114.0));
        assertThat(numOfTrx, equalTo(numOfTrx));
        assertThat(postPaidMonth, equalTo("201901"));
        assertThat(admCost, equalTo(2750.0));

    }
}

