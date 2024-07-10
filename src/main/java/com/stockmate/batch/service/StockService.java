package com.stockmate.batch.service;

import com.stockmate.batch.entity.Stock;
import com.stockmate.batch.repository.StockRepository;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void fetchAndSaveStockData() {
        System.out.println("===================================================");
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL("http://kind.krx.co.kr/corpgeneral/corpList.do?method=download&searchType=13");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true); // 리다이렉션 허용
            connection.setRequestProperty("Content-Type", "application/vnd.ms-excel");
            connection.setRequestProperty("Accept", "*/*");
            connection.connect();

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    String newUrl = connection.getHeaderField("Location");
                    connection = (HttpURLConnection) new URL(newUrl).openConnection();
                    connection.setRequestProperty("Content-Type", "application/vnd.ms-excel");
                    connection.setRequestProperty("Accept", "*/*");
                    connection.connect();
                } else {
                    System.out.println("===================================================");
                    throw new RuntimeException("Failed to download file: " + connection.getResponseMessage());
                }
            }

            inputStream = connection.getInputStream();
            Document doc = Jsoup.parse(inputStream, "euc-kr", "");
            Elements rows = doc.select("table.bbs_tb tr");
            List<Stock> stocks = extractStockList(rows);
            stockRepository.saveAll(stocks);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static List<Stock> extractStockList(Elements rows) {
        List<Stock> stocks = new ArrayList<>();
        for (Element row : rows) {
            Elements cells = row.select("td");
            if (cells.size() > 0) {
                String companyName = cells.get(0).text();
                String stockCode = cells.get(1).text();
                Stock stock = makeStockEntity(stockCode, companyName);
                stocks.add(stock);
            }
        }
        return stocks;
    }

    private static Stock makeStockEntity(String stockCode, String companyName) {
        return Stock.builder()
            .code(stockCode)
            .name(companyName)
            .koreanName(companyName)
            .build();
    }
}
