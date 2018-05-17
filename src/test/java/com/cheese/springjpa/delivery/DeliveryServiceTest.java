package com.cheese.springjpa.delivery;

import com.cheese.springjpa.Account.model.Address;
import com.cheese.springjpa.delivery.exception.DeliveryNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class DeliveryServiceTest {

    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;


    @Test
    public void create() {
        //given
        final Address address = buildAddress();
        final DeliveryDto.CreationReq dto = buildCreationDto(address);

        given(deliveryRepository.save(any(Delivery.class))).willReturn(dto.toEntity());

        //when
        final Delivery delivery = deliveryService.create(dto);

        //then
        assertThat(delivery.getAddress(), is(address));
    }

    @Test
    public void updateStatus() {
        //given
        final Address address = buildAddress();
        final DeliveryDto.CreationReq creationReq = buildCreationDto(address);
        final DeliveryDto.UpdateReq updateReq = buildUpdateReqDto();

        given(deliveryRepository.findOne(anyLong())).willReturn(creationReq.toEntity());
        //when
        final Delivery delivery = deliveryService.updateStatus(anyLong(), updateReq);

        //then

        assertThat(delivery.getAddress(), is(address));
        assertThat(delivery.getLogs().get(0).getStatus(), is(updateReq.getStatus()));
    }

    @Test
    public void findById() {

        //given
        final Address address = buildAddress();
        final DeliveryDto.CreationReq dto = buildCreationDto(address);
        given(deliveryRepository.findOne(anyLong())).willReturn(dto.toEntity());

        //when
        final Delivery delivery = deliveryService.findById(anyLong());

        //then
        assertThat(delivery.getAddress(), is(address));
        assertThat(delivery.getDateTime(), is(isNull()));


    }

    @Test(expected = DeliveryNotFoundException.class)
    public void findById_존재하지않을경우_DeliveryNotFoundException() {
        //given
        final Address address = buildAddress();
        final DeliveryDto.CreationReq dto = buildCreationDto(address);
        given(deliveryRepository.findOne(anyLong())).willReturn(null);

        //when
        deliveryService.findById(anyLong());
    }

    private DeliveryDto.UpdateReq buildUpdateReqDto() {
        return DeliveryDto.UpdateReq.builder()
                .status(DeliveryStatus.DELIVERING)
                .build();
    }

    private Address buildAddress() {
        return Address.builder()
                .address1("address1...")
                .address2("address2...")
                .zip("zip...")
                .build();
    }

    private DeliveryDto.CreationReq buildCreationDto(Address address) {
        return DeliveryDto.CreationReq.builder()
                .address(address)
                .build();
    }


}