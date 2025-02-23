import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ticket-order.test-samples';

import { TicketOrderFormService } from './ticket-order-form.service';

describe('TicketOrder Form Service', () => {
  let service: TicketOrderFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TicketOrderFormService);
  });

  describe('Service methods', () => {
    describe('createTicketOrderFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTicketOrderFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            placedDate: expect.any(Object),
            status: expect.any(Object),
            code: expect.any(Object),
            invoiceId: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });

      it('passing ITicketOrder should create a new form with FormGroup', () => {
        const formGroup = service.createTicketOrderFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            placedDate: expect.any(Object),
            status: expect.any(Object),
            code: expect.any(Object),
            invoiceId: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });
    });

    describe('getTicketOrder', () => {
      it('should return NewTicketOrder for default TicketOrder initial value', () => {
        const formGroup = service.createTicketOrderFormGroup(sampleWithNewData);

        const ticketOrder = service.getTicketOrder(formGroup) as any;

        expect(ticketOrder).toMatchObject(sampleWithNewData);
      });

      it('should return NewTicketOrder for empty TicketOrder initial value', () => {
        const formGroup = service.createTicketOrderFormGroup();

        const ticketOrder = service.getTicketOrder(formGroup) as any;

        expect(ticketOrder).toMatchObject({});
      });

      it('should return ITicketOrder', () => {
        const formGroup = service.createTicketOrderFormGroup(sampleWithRequiredData);

        const ticketOrder = service.getTicketOrder(formGroup) as any;

        expect(ticketOrder).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITicketOrder should not enable id FormControl', () => {
        const formGroup = service.createTicketOrderFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTicketOrder should disable id FormControl', () => {
        const formGroup = service.createTicketOrderFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
