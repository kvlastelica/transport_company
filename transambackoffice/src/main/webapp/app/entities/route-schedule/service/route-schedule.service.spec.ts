import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IRouteSchedule } from '../route-schedule.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../route-schedule.test-samples';

import { RestRouteSchedule, RouteScheduleService } from './route-schedule.service';

const requireRestSample: RestRouteSchedule = {
  ...sampleWithRequiredData,
  departure: sampleWithRequiredData.departure?.toJSON(),
  arrival: sampleWithRequiredData.arrival?.toJSON(),
};

describe('RouteSchedule Service', () => {
  let service: RouteScheduleService;
  let httpMock: HttpTestingController;
  let expectedResult: IRouteSchedule | IRouteSchedule[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RouteScheduleService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a RouteSchedule', () => {
      const routeSchedule = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(routeSchedule).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RouteSchedule', () => {
      const routeSchedule = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(routeSchedule).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RouteSchedule', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RouteSchedule', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RouteSchedule', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRouteScheduleToCollectionIfMissing', () => {
      it('should add a RouteSchedule to an empty array', () => {
        const routeSchedule: IRouteSchedule = sampleWithRequiredData;
        expectedResult = service.addRouteScheduleToCollectionIfMissing([], routeSchedule);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(routeSchedule);
      });

      it('should not add a RouteSchedule to an array that contains it', () => {
        const routeSchedule: IRouteSchedule = sampleWithRequiredData;
        const routeScheduleCollection: IRouteSchedule[] = [
          {
            ...routeSchedule,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRouteScheduleToCollectionIfMissing(routeScheduleCollection, routeSchedule);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RouteSchedule to an array that doesn't contain it", () => {
        const routeSchedule: IRouteSchedule = sampleWithRequiredData;
        const routeScheduleCollection: IRouteSchedule[] = [sampleWithPartialData];
        expectedResult = service.addRouteScheduleToCollectionIfMissing(routeScheduleCollection, routeSchedule);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(routeSchedule);
      });

      it('should add only unique RouteSchedule to an array', () => {
        const routeScheduleArray: IRouteSchedule[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const routeScheduleCollection: IRouteSchedule[] = [sampleWithRequiredData];
        expectedResult = service.addRouteScheduleToCollectionIfMissing(routeScheduleCollection, ...routeScheduleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const routeSchedule: IRouteSchedule = sampleWithRequiredData;
        const routeSchedule2: IRouteSchedule = sampleWithPartialData;
        expectedResult = service.addRouteScheduleToCollectionIfMissing([], routeSchedule, routeSchedule2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(routeSchedule);
        expect(expectedResult).toContain(routeSchedule2);
      });

      it('should accept null and undefined values', () => {
        const routeSchedule: IRouteSchedule = sampleWithRequiredData;
        expectedResult = service.addRouteScheduleToCollectionIfMissing([], null, routeSchedule, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(routeSchedule);
      });

      it('should return initial array if no RouteSchedule is added', () => {
        const routeScheduleCollection: IRouteSchedule[] = [sampleWithRequiredData];
        expectedResult = service.addRouteScheduleToCollectionIfMissing(routeScheduleCollection, undefined, null);
        expect(expectedResult).toEqual(routeScheduleCollection);
      });
    });

    describe('compareRouteSchedule', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRouteSchedule(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 21096 };
        const entity2 = null;

        const compareResult1 = service.compareRouteSchedule(entity1, entity2);
        const compareResult2 = service.compareRouteSchedule(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 21096 };
        const entity2 = { id: 32691 };

        const compareResult1 = service.compareRouteSchedule(entity1, entity2);
        const compareResult2 = service.compareRouteSchedule(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 21096 };
        const entity2 = { id: 21096 };

        const compareResult1 = service.compareRouteSchedule(entity1, entity2);
        const compareResult2 = service.compareRouteSchedule(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
