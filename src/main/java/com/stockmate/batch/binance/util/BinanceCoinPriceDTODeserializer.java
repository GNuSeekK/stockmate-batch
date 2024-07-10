package com.stockmate.batch.binance.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmate.batch.binance.dto.BinanceCoinPriceDTO;
import java.io.IOException;

public class BinanceCoinPriceDTODeserializer extends JsonDeserializer<BinanceCoinPriceDTO> {

    @Override
    public BinanceCoinPriceDTO deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode root = mapper.readTree(jp);
        return BinanceCoinPriceDTO.builder()
            .openTime(root.get(0).asLong())
            .openPrice(root.get(1).asDouble())
            .highPrice(root.get(2).asDouble())
            .lowPrice(root.get(3).asDouble())
            .closePrice(root.get(4).asDouble())
            .volume(root.get(5).asDouble())
            .quoteAssetVolume(root.get(7).asDouble())
            .numberOfTrades(root.get(8).asInt())
            .build();

    }
}
