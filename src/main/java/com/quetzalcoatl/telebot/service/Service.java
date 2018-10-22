package com.quetzalcoatl.telebot.service;


import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Service {
    //TODO: список всех парсеров в виде List, инициация в нон-статик блоке  -> impl
    // TODO:  все проверки на нул, эксепшены и т.д. (+ проверка корректной работы парсеров...) - тут
    //TODO: javadoc

    /**@return Возвращает ответ бота пользователю или null, если подходящего ответа нет
     */
    String getTextResponse(Update update);

    EditMessageText handleCallbackQuery(Update update);

    Message sendInlineKeyboard(long chatID);





}
