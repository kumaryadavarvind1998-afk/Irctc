package com.irctc.service;

import com.irctc.payment.repository.PaymentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.irctc.entity.BookingEntity;
import com.irctc.exception.InsufficientBalanceException;
import com.irctc.payment.entity.PaymentEntity;
import com.irctc.repository.BookingRepository;
import com.irctc.request.BookingRequest;
import com.irctc.response.BookingResponse;

import jakarta.transaction.Transactional;

@Service
public class BookingService 
{
	
	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	PaymentRepository paymentRepository;
	
	@Transactional
	public BookingResponse doBooking(BookingRequest bookingRequest)
	{
		BookingEntity bookingEntity = new BookingEntity();

		bookingEntity.setFromStation(bookingRequest.getFrom());
		bookingEntity.setToStation(bookingRequest.getTo());
		bookingEntity.setJourneyDate(bookingRequest.getDate());
		bookingEntity.setTravelClass(bookingRequest.getTravelClass());
		bookingEntity.setPassengerName(bookingRequest.getPassengerName());
		bookingEntity.setAge(bookingRequest.getAge());
		bookingEntity.setGender(bookingRequest.getGender());
		bookingEntity.setUserId(bookingRequest.getUserId());
		//bookingEntity.setPnr(generatePnr());
		bookingEntity.setStatus("Booking Ininated");
		
		// Create a booking record --1st query
		bookingEntity = bookingRepository.save(bookingEntity);
		
		// Ininate the payment
		
		PaymentEntity paymentEntity=new PaymentEntity();
		
		paymentEntity.setAmount(2000);
		paymentEntity.setBookingId(bookingEntity.getBookingId());
		paymentEntity.setTransactionId("TXN87654");
		
		try
		{
			String statusFromPG =null;
			paymentEntity.setPaymentStatus(statusFromPG.concat("Your payment is proccessing"));
		}
		
		catch (Exception e)
		{
			// TODO: handle exception
			
			e.printStackTrace();
			throw new InsufficientBalanceException("some text....");
		}
		// 2nd  
		PaymentEntity paymentEntityResponse=paymentRepository.save(paymentEntity);
		
		BookingResponse response=null;
		if(paymentEntityResponse.getPaymentId() > 0)
		{
			System.out.println("Payment is successfull");
			bookingEntity.setPnr(generatePnr());
			bookingEntity.setStatus("Ticket booked successfull");
			
			// 3rd update the booking record
			BookingEntity bookingEntityUpdated=bookingRepository.save(bookingEntity);
			
			response=new BookingResponse();
			
			response.setBookingId(bookingEntityUpdated.getBookingId());
			response.setPnrNumber(bookingEntityUpdated.getPnr());
			response.setBookingStatus("CONFIRMED"); // Or WAITING, RAC, etc.
			response.setJourneyDate(bookingEntityUpdated.getJourneyDate());
			response.setCoach("B2");
			response.setSeatNumber("32");
			response.setMessage("Ticket booked successfully.");
			
		}

		return response;

	}

	public static String generatePnr()
	{
		Random random = new Random();
		long pnr = 1000000L + (long) (random.nextDouble() * 9000000L);
		return String.valueOf(pnr);
	}
	
	
	public List<BookingResponse> getAllTickets(String userId,String pageNumber,String pageSize)
	{
		Pageable pageable=PageRequest.of(Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
		
		Page<BookingEntity> tickets=bookingRepository.findAll(pageable);  //pagination
		
		List<BookingResponse> response=new ArrayList<BookingResponse>();
		
		for(BookingEntity bookingEntity : tickets)
		{
			BookingResponse ticketResponse=new BookingResponse();
			
			ticketResponse.setBookingId(bookingEntity.getBookingId());
			ticketResponse.setPnrNumber(bookingEntity.getPnr());
			ticketResponse.setBookingStatus("CONFIRMED"); // Or WAITING, RAC, etc.
			ticketResponse.setJourneyDate(bookingEntity.getJourneyDate());
			ticketResponse.setCoach("B2");
			ticketResponse.setSeatNumber("32");
			response.add(ticketResponse);
			
		}
		
		return response;
	}
}
