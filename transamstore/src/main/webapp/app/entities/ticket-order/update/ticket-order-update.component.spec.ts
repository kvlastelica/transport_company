import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { TicketOrderService } from '../service/ticket-order.service';
import { ITicketOrder } from '../ticket-order.model';
import { TicketOrderFormService } from './ticket-order-form.service';

import { TicketOrderUpdateComponent } from './ticket-order-update.component';

describe('TicketOrder Management Update Component', () => {
  let comp: TicketOrderUpdateComponent;
  let fixture: ComponentFixture<TicketOrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ticketOrderFormService: TicketOrderFormService;
  let ticketOrderService: TicketOrderService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TicketOrderUpdateComponent],
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
      .overrideTemplate(TicketOrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TicketOrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ticketOrderFormService = TestBed.inject(TicketOrderFormService);
    ticketOrderService = TestBed.inject(TicketOrderService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Customer query and add missing value', () => {
      const ticketOrder: ITicketOrder = { id: 7942 };
      const customer: ICustomer = { id: 26915 };
      ticketOrder.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 26915 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ticketOrder });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining),
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ticketOrder: ITicketOrder = { id: 7942 };
      const customer: ICustomer = { id: 26915 };
      ticketOrder.customer = customer;

      activatedRoute.data = of({ ticketOrder });
      comp.ngOnInit();

      expect(comp.customersSharedCollection).toContainEqual(customer);
      expect(comp.ticketOrder).toEqual(ticketOrder);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicketOrder>>();
      const ticketOrder = { id: 29317 };
      jest.spyOn(ticketOrderFormService, 'getTicketOrder').mockReturnValue(ticketOrder);
      jest.spyOn(ticketOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ticketOrder }));
      saveSubject.complete();

      // THEN
      expect(ticketOrderFormService.getTicketOrder).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ticketOrderService.update).toHaveBeenCalledWith(expect.objectContaining(ticketOrder));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicketOrder>>();
      const ticketOrder = { id: 29317 };
      jest.spyOn(ticketOrderFormService, 'getTicketOrder').mockReturnValue({ id: null });
      jest.spyOn(ticketOrderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketOrder: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ticketOrder }));
      saveSubject.complete();

      // THEN
      expect(ticketOrderFormService.getTicketOrder).toHaveBeenCalled();
      expect(ticketOrderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicketOrder>>();
      const ticketOrder = { id: 29317 };
      jest.spyOn(ticketOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ticketOrderService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 26915 };
        const entity2 = { id: 21032 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
