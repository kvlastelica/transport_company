import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITicket } from 'app/entities/ticket/ticket.model';
import { TicketService } from 'app/entities/ticket/service/ticket.service';
import { ITicketOrder } from 'app/entities/ticket-order/ticket-order.model';
import { TicketOrderService } from 'app/entities/ticket-order/service/ticket-order.service';
import { IOrderItem } from '../order-item.model';
import { OrderItemService } from '../service/order-item.service';
import { OrderItemFormService } from './order-item-form.service';

import { OrderItemUpdateComponent } from './order-item-update.component';

describe('OrderItem Management Update Component', () => {
  let comp: OrderItemUpdateComponent;
  let fixture: ComponentFixture<OrderItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderItemFormService: OrderItemFormService;
  let orderItemService: OrderItemService;
  let ticketService: TicketService;
  let ticketOrderService: TicketOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OrderItemUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OrderItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderItemFormService = TestBed.inject(OrderItemFormService);
    orderItemService = TestBed.inject(OrderItemService);
    ticketService = TestBed.inject(TicketService);
    ticketOrderService = TestBed.inject(TicketOrderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Ticket query and add missing value', () => {
      const orderItem: IOrderItem = { id: 123 };
      const ticket: ITicket = { id: 29380 };
      orderItem.ticket = ticket;

      const ticketCollection: ITicket[] = [{ id: 29380 }];
      jest.spyOn(ticketService, 'query').mockReturnValue(of(new HttpResponse({ body: ticketCollection })));
      const additionalTickets = [ticket];
      const expectedCollection: ITicket[] = [...additionalTickets, ...ticketCollection];
      jest.spyOn(ticketService, 'addTicketToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(ticketService.query).toHaveBeenCalled();
      expect(ticketService.addTicketToCollectionIfMissing).toHaveBeenCalledWith(
        ticketCollection,
        ...additionalTickets.map(expect.objectContaining),
      );
      expect(comp.ticketsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TicketOrder query and add missing value', () => {
      const orderItem: IOrderItem = { id: 123 };
      const order: ITicketOrder = { id: 29317 };
      orderItem.order = order;

      const ticketOrderCollection: ITicketOrder[] = [{ id: 29317 }];
      jest.spyOn(ticketOrderService, 'query').mockReturnValue(of(new HttpResponse({ body: ticketOrderCollection })));
      const additionalTicketOrders = [order];
      const expectedCollection: ITicketOrder[] = [...additionalTicketOrders, ...ticketOrderCollection];
      jest.spyOn(ticketOrderService, 'addTicketOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(ticketOrderService.query).toHaveBeenCalled();
      expect(ticketOrderService.addTicketOrderToCollectionIfMissing).toHaveBeenCalledWith(
        ticketOrderCollection,
        ...additionalTicketOrders.map(expect.objectContaining),
      );
      expect(comp.ticketOrdersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const orderItem: IOrderItem = { id: 123 };
      const ticket: ITicket = { id: 29380 };
      orderItem.ticket = ticket;
      const order: ITicketOrder = { id: 29317 };
      orderItem.order = order;

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(comp.ticketsSharedCollection).toContainEqual(ticket);
      expect(comp.ticketOrdersSharedCollection).toContainEqual(order);
      expect(comp.orderItem).toEqual(orderItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemFormService, 'getOrderItem').mockReturnValue(orderItem);
      jest.spyOn(orderItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItem }));
      saveSubject.complete();

      // THEN
      expect(orderItemFormService.getOrderItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderItemService.update).toHaveBeenCalledWith(expect.objectContaining(orderItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemFormService, 'getOrderItem').mockReturnValue({ id: null });
      jest.spyOn(orderItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItem }));
      saveSubject.complete();

      // THEN
      expect(orderItemFormService.getOrderItem).toHaveBeenCalled();
      expect(orderItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTicket', () => {
      it('Should forward to ticketService', () => {
        const entity = { id: 29380 };
        const entity2 = { id: 23717 };
        jest.spyOn(ticketService, 'compareTicket');
        comp.compareTicket(entity, entity2);
        expect(ticketService.compareTicket).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTicketOrder', () => {
      it('Should forward to ticketOrderService', () => {
        const entity = { id: 29317 };
        const entity2 = { id: 7942 };
        jest.spyOn(ticketOrderService, 'compareTicketOrder');
        comp.compareTicketOrder(entity, entity2);
        expect(ticketOrderService.compareTicketOrder).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
