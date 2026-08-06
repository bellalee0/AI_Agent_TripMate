package com.example.tripmate.common.entity;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private String id;

    @Field("room_id")
    private Long roomId;

    @Field("sender_id")
    private Long senderId;

    private String content;

    private boolean aiCall;

    @Field("sender_nickname")
    private String senderNickname;

    @Field("sender_profile")
    private String senderProfile;

    @Field("sequence")
    private Long sequence;

    @CreatedDate
    private LocalDateTime createdDateTime;

    public ChatMessage(Long roomId, Long senderId, String content, boolean aiCall, String senderNickname, String senderProfile, Long sequence, LocalDateTime createdDatetime) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.aiCall = aiCall;
        this.senderNickname = senderNickname;
        this.senderProfile = senderProfile;
        this.sequence = sequence;
        this.createdDatetime = createdDatetime;
    }
}
