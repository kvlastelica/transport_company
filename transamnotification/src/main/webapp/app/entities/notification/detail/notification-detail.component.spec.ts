import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { NotificationDetailComponent } from './notification-detail.component';

describe('Notification Management Detail Component', () => {
  let comp: NotificationDetailComponent;
  let fixture: ComponentFixture<NotificationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./notification-detail.component').then(m => m.NotificationDetailComponent),
              resolve: { notification: () => of({ id: '77fa88cf-0751-486e-b40c-615afa341742' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(NotificationDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load notification on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', NotificationDetailComponent);

      // THEN
      expect(instance.notification()).toEqual(expect.objectContaining({ id: '77fa88cf-0751-486e-b40c-615afa341742' }));
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
