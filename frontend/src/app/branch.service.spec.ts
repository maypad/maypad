import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BranchService } from './branch.service';
import { TestBed, fakeAsync } from '@angular/core/testing';
import { environment } from '../environments/environment';
import { BuildStatus } from './model/buildStatus';
import * as get_projects_id_branches_ref_builds_response from '../../sample-requests/get.projects.id.branches.ref.builds.response.json';
import * as get_projects_id_branches_ref_deployments_r from '../../sample-requests/get.projects.id.branches.ref.deployments.response.json';
import * as get_projects_id_branches_ref_response from '../../sample-requests/get.projects.id.branches.ref.response.json';
import * as post_projects_id_branches_ref_builds_request from '../../sample-requests/post.projects.id.branches.ref.builds.request.json';
import * as post_projects_id_branches_ref_deployments_r from '../../sample-requests/post.projects.id.branches.ref.deployments.request.json';


describe('Service: BranchService', () => {
  let service: BranchService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BranchService]
    });
    service = TestBed.get(BranchService);
    httpTestingController = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('loads a build history',
    fakeAsync(() => {
      service.loadBuildHistory(2, 'master').subscribe((data) => {
        expect(data.length).toEqual(4);

        for (let i = 0; i++; i < 4) {
          expect(data[i].commit.commitIdentifier).toEqual('e37ab2d1f1eddc11b1b6531372569793bd110b83');
          expect(data[i].commit.commitMessage).toEqual('Fix various bugs');
          expect(data[i].commit.author).toEqual('Developer One <developer.one@maypad.de>');
        }

        expect(data[0].timestamp).toEqual('Wed Jan 1 14:45:30 2019 +0100');
        expect(data[0].status).toEqual(BuildStatus.SUCCESS);
        expect(data[0].commit.timestamp).toEqual('Wed Jan 1 14:37:30 2019 +0100');

        expect(data[1].timestamp).toEqual('Wed Jan 2 14:45:30 2019 +0100');
        expect(data[1].status).toEqual(BuildStatus.FAILED);
        expect(data[1].commit.timestamp).toEqual('Wed Jan 2 14:37:30 2019 +0100');

        expect(data[2].timestamp).toEqual('Wed Jan 3 14:45:30 2019 +0100');
        expect(data[2].status).toEqual(BuildStatus.FAILED);
        expect(data[2].commit.timestamp).toEqual('Wed Jan 3 14:37:30 2019 +0100');

        expect(data[3].timestamp).toEqual('Wed Jan 4 14:45:30 2019 +0100');
        expect(data[3].status).toEqual(BuildStatus.RUNNING);
        expect(data[3].commit.timestamp).toEqual('Wed Jan 4 14:37:30 2019 +0100');
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2/branches/master/builds`);
      expect(req.request.method).toEqual('GET');
      req.flush(get_projects_id_branches_ref_builds_response['default']);
    })
  );

  it('loads a deployment history',
    fakeAsync(() => {
      service.loadDeploymentHistory(2, 'master').subscribe((data) => {
        expect(data.length).toEqual(3);

        for (let i = 0; i++; i < 3) {
          expect(data[i].type).toEqual('webhook');
        }

        expect(data[0].timestamp).toEqual('Wed Jan 1 14:45:30 2019 +0100');
        expect(data[1].timestamp).toEqual('Wed Jan 2 14:45:30 2019 +0100');
        expect(data[2].timestamp).toEqual('Wed Jan 3 14:45:30 2019 +0100');
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2/branches/master/deployments`);
      expect(req.request.method).toEqual('GET');
      req.flush(get_projects_id_branches_ref_deployments_r['default']);
    })
  );

  it('loads a specific branch',
    fakeAsync(() => {
      service.loadBranch(2, 'master').subscribe((data) => {
        expect(data.name).toEqual('master');
        expect(data.readme).toEqual('# MAYPAD\nDocumentation: [https://github.com/juliantodt/'
          + 'maypad-docs](https://github.com/juliantodt/maypad-docs)');
        expect(data.status).toEqual(BuildStatus.SUCCESS);
        expect(data.lastCommit.author).toEqual('Developer One <developer.one@maypad.de>');
        expect(data.lastCommit.commitMessage).toEqual('Fix various bugs');
        expect(data.lastCommit.commitIdentifier).toEqual('e37ab2d1f1eddc11b1b6531372569793bd110b83');
        expect(data.lastCommit.timestamp).toEqual('Wed Jan 2 14:37:30 2019 +0100');
        expect(data.members).toEqual(['Developer One', 'Developer Two']);
        expect(data.mails).toEqual(['developer.one@maypad.de', 'developer.two@maypad.de']);
        expect(data.buildSuccessUrl).toEqual('https://maypad.de/projects/1/branches/master/'
          + 'builds/success?token=djhbvjskbfavhbkdfbvlfbva');
        expect(data.buildFailUrl).toEqual('https://maypad.de/projects/1/branches/master/builds/fail?token=jhdsbfajdbfhjbhvjdafb');
        expect(data.dependencies).toEqual(['3:master', '12:dev', '24:master']);
        expect(data.deploymentWebhook).toEqual('webhook: https://ship.maypad.de/hook?token=vjfsdbjhkvlfavhkl');
        expect(data.buildWebhook).toEqual('https://git.maypad.de/pipeline-hook?token=kjadbhjasebjkdsa');
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2/branches/master`);
      expect(req.request.method).toEqual('GET');
      req.flush(get_projects_id_branches_ref_response['default']);
    })
  );

  it('triggers a build',
    fakeAsync(() => {
      service.triggerBuild(2, 'master', true).subscribe();

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2/branches/master/builds`);
      expect(req.request.method).toEqual('POST');
      expect(JSON.parse(req.request.body)).toEqual(post_projects_id_branches_ref_builds_request['default']);
      req.flush(null, { status: 202, statusText: 'Accepted' });
    })
  );

  it('triggers a deployment',
    fakeAsync(() => {
      service.triggerDeployment(2, 'master', true, false).subscribe();

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2/branches/master/deployments`);
      expect(req.request.method).toEqual('POST');
      expect(JSON.parse(req.request.body)).toEqual(post_projects_id_branches_ref_deployments_r['default']);
      req.flush(null, { status: 202, statusText: 'Accepted' });
    })
  );
});
