import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';
import { IVehicle } from 'app/entities/vehicle/vehicle.model';
import { VehicleService } from 'app/entities/vehicle/service/vehicle.service';
import { IRoute } from '../route.model';
import { RouteService } from '../service/route.service';
import { RouteFormService } from './route-form.service';

import { RouteUpdateComponent } from './route-update.component';

describe('Route Management Update Component', () => {
  let comp: RouteUpdateComponent;
  let fixture: ComponentFixture<RouteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let routeFormService: RouteFormService;
  let routeService: RouteService;
  let employeeService: EmployeeService;
  let vehicleService: VehicleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouteUpdateComponent],
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
      .overrideTemplate(RouteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RouteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    routeFormService = TestBed.inject(RouteFormService);
    routeService = TestBed.inject(RouteService);
    employeeService = TestBed.inject(EmployeeService);
    vehicleService = TestBed.inject(VehicleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Employee query and add missing value', () => {
      const route: IRoute = { id: 19862 };
      const employees: IEmployee[] = [{ id: 1749 }];
      route.employees = employees;

      const employeeCollection: IEmployee[] = [{ id: 1749 }];
      jest.spyOn(employeeService, 'query').mockReturnValue(of(new HttpResponse({ body: employeeCollection })));
      const additionalEmployees = [...employees];
      const expectedCollection: IEmployee[] = [...additionalEmployees, ...employeeCollection];
      jest.spyOn(employeeService, 'addEmployeeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ route });
      comp.ngOnInit();

      expect(employeeService.query).toHaveBeenCalled();
      expect(employeeService.addEmployeeToCollectionIfMissing).toHaveBeenCalledWith(
        employeeCollection,
        ...additionalEmployees.map(expect.objectContaining),
      );
      expect(comp.employeesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Vehicle query and add missing value', () => {
      const route: IRoute = { id: 19862 };
      const vehicles: IVehicle[] = [{ id: 18638 }];
      route.vehicles = vehicles;

      const vehicleCollection: IVehicle[] = [{ id: 18638 }];
      jest.spyOn(vehicleService, 'query').mockReturnValue(of(new HttpResponse({ body: vehicleCollection })));
      const additionalVehicles = [...vehicles];
      const expectedCollection: IVehicle[] = [...additionalVehicles, ...vehicleCollection];
      jest.spyOn(vehicleService, 'addVehicleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ route });
      comp.ngOnInit();

      expect(vehicleService.query).toHaveBeenCalled();
      expect(vehicleService.addVehicleToCollectionIfMissing).toHaveBeenCalledWith(
        vehicleCollection,
        ...additionalVehicles.map(expect.objectContaining),
      );
      expect(comp.vehiclesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const route: IRoute = { id: 19862 };
      const employee: IEmployee = { id: 1749 };
      route.employees = [employee];
      const vehicle: IVehicle = { id: 18638 };
      route.vehicles = [vehicle];

      activatedRoute.data = of({ route });
      comp.ngOnInit();

      expect(comp.employeesSharedCollection).toContainEqual(employee);
      expect(comp.vehiclesSharedCollection).toContainEqual(vehicle);
      expect(comp.route).toEqual(route);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRoute>>();
      const route = { id: 9151 };
      jest.spyOn(routeFormService, 'getRoute').mockReturnValue(route);
      jest.spyOn(routeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ route });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: route }));
      saveSubject.complete();

      // THEN
      expect(routeFormService.getRoute).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(routeService.update).toHaveBeenCalledWith(expect.objectContaining(route));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRoute>>();
      const route = { id: 9151 };
      jest.spyOn(routeFormService, 'getRoute').mockReturnValue({ id: null });
      jest.spyOn(routeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ route: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: route }));
      saveSubject.complete();

      // THEN
      expect(routeFormService.getRoute).toHaveBeenCalled();
      expect(routeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRoute>>();
      const route = { id: 9151 };
      jest.spyOn(routeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ route });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(routeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEmployee', () => {
      it('Should forward to employeeService', () => {
        const entity = { id: 1749 };
        const entity2 = { id: 1545 };
        jest.spyOn(employeeService, 'compareEmployee');
        comp.compareEmployee(entity, entity2);
        expect(employeeService.compareEmployee).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareVehicle', () => {
      it('Should forward to vehicleService', () => {
        const entity = { id: 18638 };
        const entity2 = { id: 22559 };
        jest.spyOn(vehicleService, 'compareVehicle');
        comp.compareVehicle(entity, entity2);
        expect(vehicleService.compareVehicle).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
