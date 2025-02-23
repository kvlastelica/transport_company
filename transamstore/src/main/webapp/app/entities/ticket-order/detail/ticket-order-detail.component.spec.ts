import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TicketOrderDetailComponent } from './ticket-order-detail.component';

describe('TicketOrder Management Detail Component', () => {
  let comp: TicketOrderDetailComponent;
  let fixture: ComponentFixture<TicketOrderDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketOrderDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./ticket-order-detail.component').then(m => m.TicketOrderDetailComponent),
              resolve: { ticketOrder: () => of({ id: 29317 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TicketOrderDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketOrderDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ticketOrder on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TicketOrderDetailComponent);

      // THEN
      expect(instance.ticketOrder()).toEqual(expect.objectContaining({ id: 29317 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
