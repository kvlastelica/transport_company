import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITicketOrder } from '../ticket-order.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../ticket-order.test-samples';

import { RestTicketOrder, TicketOrderService } from './ticket-order.service';

const requireRestSample: RestTicketOrder = {
  ...sampleWithRequiredData,
  placedDate: sampleWithRequiredData.placedDate?.toJSON(),
};

describe('TicketOrder Service', () => {
  let service: TicketOrderService;
  let httpMock: HttpTestingController;
  let expectedResult: ITicketOrder | ITicketOrder[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TicketOrderService);
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

    it('should create a TicketOrder', () => {
      const ticketOrder = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ticketOrder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TicketOrder', () => {
      const ticketOrder = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ticketOrder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TicketOrder', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TicketOrder', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TicketOrder', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTicketOrderToCollectionIfMissing', () => {
      it('should add a TicketOrder to an empty array', () => {
        const ticketOrder: ITicketOrder = sampleWithRequiredData;
        expectedResult = service.addTicketOrderToCollectionIfMissing([], ticketOrder);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ticketOrder);
      });

      it('should not add a TicketOrder to an array that contains it', () => {
        const ticketOrder: ITicketOrder = sampleWithRequiredData;
        const ticketOrderCollection: ITicketOrder[] = [
          {
            ...ticketOrder,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTicketOrderToCollectionIfMissing(ticketOrderCollection, ticketOrder);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TicketOrder to an array that doesn't contain it", () => {
        const ticketOrder: ITicketOrder = sampleWithRequiredData;
        const ticketOrderCollection: ITicketOrder[] = [sampleWithPartialData];
        expectedResult = service.addTicketOrderToCollectionIfMissing(ticketOrderCollection, ticketOrder);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ticketOrder);
      });

      it('should add only unique TicketOrder to an array', () => {
        const ticketOrderArray: ITicketOrder[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ticketOrderCollection: ITicketOrder[] = [sampleWithRequiredData];
        expectedResult = service.addTicketOrderToCollectionIfMissing(ticketOrderCollection, ...ticketOrderArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ticketOrder: ITicketOrder = sampleWithRequiredData;
        const ticketOrder2: ITicketOrder = sampleWithPartialData;
        expectedResult = service.addTicketOrderToCollectionIfMissing([], ticketOrder, ticketOrder2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ticketOrder);
        expect(expectedResult).toContain(ticketOrder2);
      });

      it('should accept null and undefined values', () => {
        const ticketOrder: ITicketOrder = sampleWithRequiredData;
        expectedResult = service.addTicketOrderToCollectionIfMissing([], null, ticketOrder, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ticketOrder);
      });

      it('should return initial array if no TicketOrder is added', () => {
        const ticketOrderCollection: ITicketOrder[] = [sampleWithRequiredData];
        expectedResult = service.addTicketOrderToCollectionIfMissing(ticketOrderCollection, undefined, null);
        expect(expectedResult).toEqual(ticketOrderCollection);
      });
    });

    describe('compareTicketOrder', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTicketOrder(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 29317 };
        const entity2 = null;

        const compareResult1 = service.compareTicketOrder(entity1, entity2);
        const compareResult2 = service.compareTicketOrder(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 29317 };
        const entity2 = { id: 7942 };

        const compareResult1 = service.compareTicketOrder(entity1, entity2);
        const compareResult2 = service.compareTicketOrder(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 29317 };
        const entity2 = { id: 29317 };

        const compareResult1 = service.compareTicketOrder(entity1, entity2);
        const compareResult2 = service.compareTicketOrder(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
