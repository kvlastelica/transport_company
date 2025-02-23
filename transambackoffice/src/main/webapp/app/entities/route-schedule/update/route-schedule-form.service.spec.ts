import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../route-schedule.test-samples';

import { RouteScheduleFormService } from './route-schedule-form.service';

describe('RouteSchedule Form Service', () => {
  let service: RouteScheduleFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RouteScheduleFormService);
  });

  describe('Service methods', () => {
    describe('createRouteScheduleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRouteScheduleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            description: expect.any(Object),
            departure: expect.any(Object),
            arrival: expect.any(Object),
            route: expect.any(Object),
          }),
        );
      });

      it('passing IRouteSchedule should create a new form with FormGroup', () => {
        const formGroup = service.createRouteScheduleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            description: expect.any(Object),
            departure: expect.any(Object),
            arrival: expect.any(Object),
            route: expect.any(Object),
          }),
        );
      });
    });

    describe('getRouteSchedule', () => {
      it('should return NewRouteSchedule for default RouteSchedule initial value', () => {
        const formGroup = service.createRouteScheduleFormGroup(sampleWithNewData);

        const routeSchedule = service.getRouteSchedule(formGroup) as any;

        expect(routeSchedule).toMatchObject(sampleWithNewData);
      });

      it('should return NewRouteSchedule for empty RouteSchedule initial value', () => {
        const formGroup = service.createRouteScheduleFormGroup();

        const routeSchedule = service.getRouteSchedule(formGroup) as any;

        expect(routeSchedule).toMatchObject({});
      });

      it('should return IRouteSchedule', () => {
        const formGroup = service.createRouteScheduleFormGroup(sampleWithRequiredData);

        const routeSchedule = service.getRouteSchedule(formGroup) as any;

        expect(routeSchedule).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRouteSchedule should not enable id FormControl', () => {
        const formGroup = service.createRouteScheduleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRouteSchedule should disable id FormControl', () => {
        const formGroup = service.createRouteScheduleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
