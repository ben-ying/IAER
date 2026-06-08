package com.yjh.iaer.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.yjh.iaer.room.entity.Category;

import java.lang.reflect.Type;

public class CategoryDeserializer implements JsonDeserializer<Category> {

    @Override
    public Category deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        Category category = new Category();
        if (!json.isJsonObject()) {
            return category;
        }
        JsonObject object = json.getAsJsonObject();
        if (object.has(Category.FIELD_CATEGORY_ID)) {
            category.setCategoryId(object.get(Category.FIELD_CATEGORY_ID).getAsInt());
        }
        if (object.has(Category.FIELD_NAME)) {
            category.setName(object.get(Category.FIELD_NAME).getAsString());
        }
        if (object.has(Category.FIELD_SEQUENCE)) {
            category.setSequence(object.get(Category.FIELD_SEQUENCE).getAsInt());
        }
        if (object.has(Category.FIELD_YEAR)) {
            category.setYear(object.get(Category.FIELD_YEAR).getAsInt());
        }
        if (object.has(Category.FIELD_MONTH)) {
            category.setMonth(object.get(Category.FIELD_MONTH).getAsInt());
        }
        if (object.has(Category.FIELD_CREATED) && !object.get(Category.FIELD_CREATED).isJsonNull()) {
            category.setCreated(object.get(Category.FIELD_CREATED).getAsString());
        }
        if (object.has(Category.FIELD_MODIFIED) && !object.get(Category.FIELD_MODIFIED).isJsonNull()) {
            category.setModified(object.get(Category.FIELD_MODIFIED).getAsString());
        }
        if (object.has(Category.FIELD_MONEY) && !object.get(Category.FIELD_MONEY).isJsonNull()) {
            double money = parseMoney(object.get(Category.FIELD_MONEY));
            category.setPreciseMoney(money);
            category.setMoney((int) Math.round(money));
        }
        return category;
    }

    private double parseMoney(JsonElement element) {
        if (element.isJsonPrimitive()) {
            if (element.getAsJsonPrimitive().isString()) {
                try {
                    return Double.parseDouble(element.getAsString());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            return element.getAsDouble();
        }
        return 0;
    }
}
