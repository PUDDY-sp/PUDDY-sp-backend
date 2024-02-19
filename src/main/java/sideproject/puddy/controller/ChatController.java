package sideproject.puddy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sideproject.puddy.dto.chat.response.ChatDetailResponse;
import sideproject.puddy.dto.chat.response.GetChatListResponse;
import sideproject.puddy.service.ChatAndMessageService;
import sideproject.puddy.service.ChatService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {
    
    private final ChatService chatService;
    private final ChatAndMessageService chatAndMessageService;

    @GetMapping("/chat")
    public GetChatListResponse getChatList(){
        return chatService.getChatList();
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<ChatDetailResponse> enterChat(@PathVariable Long chatId){
        return chatAndMessageService.enterChat(chatId);
    }

    @DeleteMapping("/chat/{chatId}")
    public ResponseEntity<String> deleteChat(@PathVariable Long chatId){
        return chatService.deleteChat(chatId);
    }
}
