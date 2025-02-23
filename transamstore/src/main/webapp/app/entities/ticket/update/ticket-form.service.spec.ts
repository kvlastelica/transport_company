import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ticket.test-samples';

import { TicketFormService } from './ticket-form.service';

describe('Ticket Form Service', () => {
  let service: TicketFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TicketFormService);
  });

  describe('Service methods', () => {
    describe('createTicketFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTicketFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            price: expect.any(Object),
            productSize: expect.any(Object),
            image: expect.any(Object),
          }),
        );
      });

      it('passing ITicket should create a new form with FormGroup', () => {
        const formGroup = service.createTicketFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            price: expect.any(Object),
            productSize: expect.any(Object),
            image: expect.any(Object),
          }),
        );
      });
    });

    describe('getTicket', () => {
      it('should return NewTicket for default Ticket initial value', () => {
        const formGroup = service.createTicketFormGroup(sampleWithNewData);

        const ticket = service.getTicket(formGroup) as any;

        expect(ticket).toMatchObject(sampleWithNewData);
      });

      it('should return NewTicket for empty Ticket initial value', () => {
        const formGroup = service.createTicketFormGroup();

        const ticket = service.getTicket(formGroup) as any;

        expect(ticket).toMatchObject({});
      });

      it('should return ITicket', () => {
        const formGroup = service.createTicketFormGroup(sampleWithRequiredData);

        const ticket = service.getTicket(formGroup) as any;

        expect(ticket).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITicket should not enable id FormControl', () => {
        const formGroup = service.createTicketFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTicket should disable id FormControl', () => {
        const formGroup = service.createTicketFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
