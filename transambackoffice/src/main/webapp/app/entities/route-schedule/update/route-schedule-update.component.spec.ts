import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IRoute } from 'app/entities/route/route.model';
import { RouteService } from 'app/entities/route/service/route.service';
import { RouteScheduleService } from '../service/route-schedule.service';
import { IRouteSchedule } from '../route-schedule.model';
import { RouteScheduleFormService } from './route-schedule-form.service';

import { RouteScheduleUpdateComponent } from './route-schedule-update.component';

describe('RouteSchedule Management Update Component', () => {
  let comp: RouteScheduleUpdateComponent;
  let fixture: ComponentFixture<RouteScheduleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let routeScheduleFormService: RouteScheduleFormService;
  let routeScheduleService: RouteScheduleService;
  let routeService: RouteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouteScheduleUpdateComponent],
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
      .overrideTemplate(RouteScheduleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RouteScheduleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    routeScheduleFormService = TestBed.inject(RouteScheduleFormService);
    routeScheduleService = TestBed.inject(RouteScheduleService);
    routeService = TestBed.inject(RouteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Route query and add missing value', () => {
      const routeSchedule: IRouteSchedule = { id: 32691 };
      const route: IRoute = { id: 9151 };
      routeSchedule.route = route;

      const routeCollection: IRoute[] = [{ id: 9151 }];
      jest.spyOn(routeService, 'query').mockReturnValue(of(new HttpResponse({ body: routeCollection })));
      const additionalRoutes = [route];
      const expectedCollection: IRoute[] = [...additionalRoutes, ...routeCollection];
      jest.spyOn(routeService, 'addRouteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ routeSchedule });
      comp.ngOnInit();

      expect(routeService.query).toHaveBeenCalled();
      expect(routeService.addRouteToCollectionIfMissing).toHaveBeenCalledWith(
        routeCollection,
        ...additionalRoutes.map(expect.objectContaining),
      );
      expect(comp.routesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const routeSchedule: IRouteSchedule = { id: 32691 };
      const route: IRoute = { id: 9151 };
      routeSchedule.route = route;

      activatedRoute.data = of({ routeSchedule });
      comp.ngOnInit();

      expect(comp.routesSharedCollection).toContainEqual(route);
      expect(comp.routeSchedule).toEqual(routeSchedule);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRouteSchedule>>();
      const routeSchedule = { id: 21096 };
      jest.spyOn(routeScheduleFormService, 'getRouteSchedule').mockReturnValue(routeSchedule);
      jest.spyOn(routeScheduleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routeSchedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: routeSchedule }));
      saveSubject.complete();

      // THEN
      expect(routeScheduleFormService.getRouteSchedule).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(routeScheduleService.update).toHaveBeenCalledWith(expect.objectContaining(routeSchedule));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRouteSchedule>>();
      const routeSchedule = { id: 21096 };
      jest.spyOn(routeScheduleFormService, 'getRouteSchedule').mockReturnValue({ id: null });
      jest.spyOn(routeScheduleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routeSchedule: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: routeSchedule }));
      saveSubject.complete();

      // THEN
      expect(routeScheduleFormService.getRouteSchedule).toHaveBeenCalled();
      expect(routeScheduleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRouteSchedule>>();
      const routeSchedule = { id: 21096 };
      jest.spyOn(routeScheduleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routeSchedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(routeScheduleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRoute', () => {
      it('Should forward to routeService', () => {
        const entity = { id: 9151 };
        const entity2 = { id: 19862 };
        jest.spyOn(routeService, 'compareRoute');
        comp.compareRoute(entity, entity2);
        expect(routeService.compareRoute).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
