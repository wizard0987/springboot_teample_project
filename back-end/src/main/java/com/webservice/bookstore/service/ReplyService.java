package com.webservice.bookstore.service;

import com.webservice.bookstore.domain.entity.board.Board;
import com.webservice.bookstore.domain.entity.board.BoardRepository;
import com.webservice.bookstore.domain.entity.member.Member;
import com.webservice.bookstore.domain.entity.member.MemberRepository;
import com.webservice.bookstore.domain.entity.reply.Reply;
import com.webservice.bookstore.domain.entity.reply.ReplyRepository;
import com.webservice.bookstore.web.dto.BoardDTO;
import com.webservice.bookstore.web.dto.ReplyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.font.OpenType;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    //댓글등록
    public void registerReply(ReplyDTO replyDTO){
        if(replyDTO.getDepth()==0)
            replyDTO.setGroupOrder(replyRepository.getReplyOrder(replyDTO.getBoardId())+1);
        Board board = boardRepository.getOne(replyDTO.getBoardId());
        Optional<Member> op = memberRepository.findByEmail(replyDTO.getMemberEmail());

        if(!op.isPresent()) {
            throw new IllegalStateException("없는 이메일 완전에러");
        }
        Member member = op.get();
        replyDTO.setNickName(member.getNickName());
        Reply reply =  ReplyDTO.toEntity(replyDTO,board);
        replyRepository.save(reply);
    }


    //댓글 정보들 보여주기
    public List<ReplyDTO> getBoardReplylist(BoardDTO boardDTO){
        List<Reply> list=replyRepository.getBoardReplyList(boardDTO.getId());
        List<Reply> result=new ArrayList<>();
        replyOrder(result,list);
        List<ReplyDTO> dtoList=result.stream().map(reply -> ReplyDTO.entityToDTO(reply)).collect(Collectors.toList());
        return dtoList;
    }

    //댓글 정보들 보여주기
    public List<ReplyDTO> getBoardReplyList(Long boardId){
        List<Reply> list=replyRepository.getBoardReplyList(boardId);
        List<Reply> result=new ArrayList<>();
        replyOrder(result,list);
        List<ReplyDTO> dtoList=result.stream().map(reply -> ReplyDTO.entityToDTO(reply)).collect(Collectors.toList());
        return dtoList;
    }

    private void replyOrder(List<Reply> result,List<Reply> list){

        HashMap<Long,List<Reply>> hash = new HashMap<>();
        List<Reply> orderList = new ArrayList<>();

        for(int i=0;i<list.size();i++){
            if(list.get(i).getParent()==0){
                orderList.add(list.get(i));
            }
            else
            {
                List<Reply> list2 = hash.get(list.get(i).getParent());
                if(list2==null){
                    list2 = new ArrayList<Reply>();
                }
                list2.add(list.get(i));
                hash.put(list.get(i).getParent(),list2);
            }
        }

        for(int i=0;i<orderList.size();i++){
            backtrack(hash,orderList.get(i),result);
        }

    }
    private void backtrack(HashMap<Long,List<Reply>> hash,Reply cur,List<Reply> result){
        List<Reply> list = hash.get(cur.getId());
        result.add(cur);
        if(list==null)
            return;
        for(int i=0;i<list.size();i++){
            backtrack(hash,list.get(i),result);
        }
    }


    @Modifying
    @Transactional
    public boolean changeReply(ReplyDTO replyDTO,String loginEmail){

        Reply reply = replyRepository.getOne(replyDTO.getId());
        if(loginEmail==null || loginEmail.length()==0)
            return false;
        if(!reply.getMemberEmail().equals(loginEmail))
            return false;

        reply.setContent(replyDTO.getContent());
        replyRepository.save(reply);
        return true;
    }

    @Transactional
    @Modifying
    public boolean deleteReply(ReplyDTO replyDTO,String loginEmail) {
        Reply reply = replyRepository.getOne(replyDTO.getId());
        if (loginEmail == null || loginEmail.length() == 0)
            return false;
        if (!reply.getMemberEmail().equals(loginEmail))
            return false;
        replyRepository.deleteById(replyDTO.getId());
        return true;
    }
}
