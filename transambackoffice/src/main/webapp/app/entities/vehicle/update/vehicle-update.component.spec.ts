import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IRoute } from 'app/entities/route/route.model';
import { RouteService } from 'app/entities/route/service/route.service';
import { VehicleService } from '../service/vehicle.service';
import { IVehicle } from '../vehicle.model';
import { VehicleFormService } from './vehicle-form.service';

import { VehicleUpdateComponent } from './vehicle-update.component';

describe('Vehicle Management Update Component', () => {
  let comp: VehicleUpdateComponent;
  let fixture: ComponentFixture<VehicleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let vehicleFormService: VehicleFormService;
  let vehicleService: VehicleService;
  let routeService: RouteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VehicleUpdateComponent],
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
      .overrideTemplate(VehicleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VehicleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    vehicleFormService = TestBed.inject(VehicleFormService);
    vehicleService = TestBed.inject(VehicleService);
    routeService = TestBed.inject(RouteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Route query and add missing value', () => {
      const vehicle: IVehicle = { id: 22559 };
      const routes: IRoute[] = [{ id: 9151 }];
      vehicle.routes = routes;

      const routeCollection: IRoute[] = [{ id: 9151 }];
      jest.spyOn(routeService, 'query').mockReturnValue(of(new HttpResponse({ body: routeCollection })));
      const additionalRoutes = [...routes];
      const expectedCollection: IRoute[] = [...additionalRoutes, ...routeCollection];
      jest.spyOn(routeService, 'addRouteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ vehicle });
      comp.ngOnInit();

      expect(routeService.query).toHaveBeenCalled();
      expect(routeService.addRouteToCollectionIfMissing).toHaveBeenCalledWith(
        routeCollection,
        ...additionalRoutes.map(expect.objectContaining),
      );
      expect(comp.routesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const vehicle: IVehicle = { id: 22559 };
      const route: IRoute = { id: 9151 };
      vehicle.routes = [route];

      activatedRoute.data = of({ vehicle });
      comp.ngOnInit();

      expect(comp.routesSharedCollection).toContainEqual(route);
      expect(comp.vehicle).toEqual(vehicle);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVehicle>>();
      const vehicle = { id: 18638 };
      jest.spyOn(vehicleFormService, 'getVehicle').mockReturnValue(vehicle);
      jest.spyOn(vehicleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehicle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vehicle }));
      saveSubject.complete();

      // THEN
      expect(vehicleFormService.getVehicle).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(vehicleService.update).toHaveBeenCalledWith(expect.objectContaining(vehicle));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVehicle>>();
      const vehicle = { id: 18638 };
      jest.spyOn(vehicleFormService, 'getVehicle').mockReturnValue({ id: null });
      jest.spyOn(vehicleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehicle: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vehicle }));
      saveSubject.complete();

      // THEN
      expect(vehicleFormService.getVehicle).toHaveBeenCalled();
      expect(vehicleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVehicle>>();
      const vehicle = { id: 18638 };
      jest.spyOn(vehicleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehicle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(vehicleService.update).toHaveBeenCalled();
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
